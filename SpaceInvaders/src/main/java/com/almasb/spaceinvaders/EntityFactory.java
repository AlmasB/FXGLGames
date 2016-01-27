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

package com.almasb.spaceinvaders;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.spaceinvaders.component.InvincibleComponent;
import com.almasb.spaceinvaders.component.OwnerComponent;
import com.almasb.spaceinvaders.control.EnemyControl;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class EntityFactory {

    public enum EntityType {
        PLAYER, ENEMY, BULLET
    }

    private static final AssetLoader assetLoader = GameApplication.getService(ServiceType.ASSET_LOADER);

    private static Texture textureExplosion;

    // we don't have to do this in code
    // we could just pre-format the texture specifically for the game
    public static void preLoad() {
        if (textureExplosion != null)
            return;

        textureExplosion = assetLoader.loadTexture("explosion.png");
        int h = 1536 / 6;
        Texture textureCombined = textureExplosion.subTexture(new Rectangle2D(0, 0, 2048, h));

        for (int i = 1; i < 6; i++) {
            textureCombined = textureCombined
                    .superTexture(textureExplosion.subTexture(new Rectangle2D(0, h*i, 2048, h)), HorizontalDirection.RIGHT);
        }

        textureExplosion = textureCombined;
    }

    public static Entity newBackground(double w, double h) {
        GameEntity bg = new GameEntity();
        Texture bgTexture = assetLoader.loadTexture("background.png");
        bgTexture.setFitWidth(w);
        bgTexture.setFitHeight(h);

        bg.getMainViewComponent().setGraphics(bgTexture);

        return bg;
    }

    public static GameEntity newPlayer(double x, double y) {
        GameEntity player = new GameEntity();
        player.getTypeComponent().setValue(EntityType.PLAYER);
        player.getPositionComponent().setValue(x, y);

        Texture texture = assetLoader.loadTexture("player.png");

        player.getMainViewComponent().setView(new EntityView(texture), true);

        player.addComponent(new CollidableComponent(true));
        player.addComponent(new InvincibleComponent());

        return player;
    }

    public static Entity newEnemy(double x, double y) {
        GameEntity enemy = new GameEntity();
        enemy.getTypeComponent().setValue(EntityType.ENEMY);
        enemy.getPositionComponent().setValue(x, y);

        Texture texture = assetLoader.loadTexture("enemy" + ((int)(Math.random() * 3) + 1) + ".png")
                .toStaticAnimatedTexture(2, Duration.seconds(2));

        enemy.getMainViewComponent().setView(new EntityView(texture), true);
        enemy.addComponent(new CollidableComponent(true));
        enemy.addControl(new EnemyControl());

        return enemy;
    }

    public static Entity newBullet(Entity owner) {
        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(EntityType.BULLET);

        Point2D center = Entities.getBBox(owner)
                .getCenterWorld()
                .add(-8, 20 * (Entities.getType(owner).isType(EntityType.PLAYER) ? -1 : 1));

        bullet.getPositionComponent().setValue(center);

        bullet.addComponent(new CollidableComponent(true));
        bullet.getMainViewComponent().setView(new EntityView(assetLoader.loadTexture("tank_bullet.png")), true);
        bullet.addControl(new ProjectileControl(new Point2D(0, Entities.getType(owner).isType(EntityType.PLAYER) ? -1 : 1), 10));
        bullet.addComponent(new OwnerComponent(Entities.getType(owner).getValue()));
        bullet.addControl(new OffscreenCleanControl());

        return bullet;
    }

    public static Entity newExplosion(Point2D position) {
        GameEntity explosion = new GameEntity();
        explosion.getPositionComponent().setValue(position.subtract(40, 40));

        Texture animation = textureExplosion.toStaticAnimatedTexture(48, Duration.seconds(2));
        animation.setFitWidth(80);
        animation.setFitHeight(80);

        explosion.getMainViewComponent().setGraphics(animation);
        explosion.addControl(new ExpireCleanControl(Duration.seconds(1.8)));

        return explosion;
    }
}
