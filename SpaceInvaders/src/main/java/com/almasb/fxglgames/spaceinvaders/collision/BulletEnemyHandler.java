/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.spaceinvaders.collision;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import com.almasb.fxglgames.spaceinvaders.components.OwnerComponent;
import com.almasb.fxglgames.spaceinvaders.components.BossComponent;
import com.almasb.fxglgames.spaceinvaders.components.EnemyComponent;
import com.almasb.fxglgames.spaceinvaders.components.HealthComponent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(SpaceInvadersType.BULLET, SpaceInvadersType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {
        Object owner = bullet.getComponent(OwnerComponent.class).getValue();

        // some enemy shot the bullet, skip collision handling
        if (owner == SpaceInvadersType.ENEMY) {
            return;
        }

        bullet.removeFromWorld();

        HealthComponent hp = enemy.getComponent(HealthComponent.class);
        hp.setValue(hp.getValue() - 1);

        if (hp.getValue() <= 0) {

            if (enemy.hasComponent(EnemyComponent.class)) {
                enemy.getComponent(EnemyComponent.class).die();
            } else {
                enemy.getComponent(BossComponent.class).die();
            }

        } else {
            enemy.getComponentOptional(EffectComponent.class).ifPresent(e -> e.startEffect(new Effect(Duration.seconds(1)) {
                @Override
                public void onStart(Entity entity) {
                    entity.getComponent(TimeComponent.class).setValue(0.15);
                }

                @Override
                public void onEnd(Entity entity) {
                    entity.getComponent(TimeComponent.class).setValue(1);
                }
            }));
        }
    }
}
