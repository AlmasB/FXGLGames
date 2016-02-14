/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.spaceinvaders.collision;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.spaceinvaders.EntityFactory;
import com.almasb.spaceinvaders.component.HPComponent;
import com.almasb.spaceinvaders.component.InvincibleComponent;
import com.almasb.spaceinvaders.component.OwnerComponent;
import com.almasb.spaceinvaders.event.GameEvent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(EntityFactory.EntityType.BULLET, EntityFactory.EntityType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        Object owner = bullet.getComponentUnsafe(OwnerComponent.class).getValue();

        // some enemy shot the bullet, skip collision handling
        if (owner == EntityFactory.EntityType.ENEMY) {
            return;
        }

        Point2D hitPosition = bullet.getComponentUnsafe(PositionComponent.class).getValue();
        bullet.removeFromWorld();

        HPComponent hp = enemy.getComponentUnsafe(HPComponent.class);
        hp.setValue(hp.getValue() - 1);

        if (hp.getValue() <= 0) {
            Entity explosion = EntityFactory.newExplosion(Entities.getBBox(enemy).getCenterWorld());
            enemy.getWorld().addEntity(explosion);

            enemy.removeFromWorld();

            // TODO: do this via a listener to entity world, i.e. when they are actually removed
            GameApplication.getService(ServiceType.AUDIO_PLAYER).playSound("explosion.wav");
            GameApplication.getService(ServiceType.EVENT_BUS).fireEvent(new GameEvent(GameEvent.ENEMY_KILLED));
        } else {
            Entity laserHit = EntityFactory.newLaserHit(hitPosition);

            enemy.getWorld().addEntity(laserHit);

            enemy.getComponentUnsafe(MainViewComponent.class).getView().setBlendMode(BlendMode.RED);

            GameApplication.getService(ServiceType.MASTER_TIMER)
                    .runOnceAfter(() -> {
                        if (enemy.isActive())
                            enemy.getComponentUnsafe(MainViewComponent.class).getView().setBlendMode(null);
                    }, Duration.seconds(0.33));
        }
    }
}
