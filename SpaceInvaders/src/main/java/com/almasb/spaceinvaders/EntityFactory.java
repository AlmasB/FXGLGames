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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.control.ProjectileControl;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class EntityFactory {

    public enum Type implements EntityType {
        PLAYER, ENEMY, BULLET,
        LEVEL_INFO
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

    public static Entity newPlayer(double x, double y) {
        Entity player = new Entity(Type.PLAYER);
        player.setPosition(x, y);

        Texture texture = assetLoader.loadTexture("tank_player.png");
        texture.setFitWidth(40);
        texture.setFitHeight(40);

        player.setSceneView(texture);
        player.setCollidable(true);
        player.setRotation(-90);

        player.addComponent(new InvincibleComponent());

        return player;
    }

    public static Entity newEnemy(double x, double y) {
        Entity enemy = new Entity(Type.ENEMY);
        enemy.setPosition(x, y);

        Texture texture = assetLoader.loadTexture("tank_enemy.png");
        texture.setFitWidth(40);
        texture.setFitHeight(40);

        enemy.setSceneView(texture);
        enemy.setCollidable(true);
        enemy.setRotation(90);
        enemy.addControl(new EnemyControl());

        return enemy;
    }

    public static Entity newBullet(Entity owner) {
        Entity bullet = new Entity(Type.BULLET);
        bullet.setPosition(owner.getCenter().add(-8, 20 * (owner.isType(Type.PLAYER) ? -1 : 1)));
        bullet.setCollidable(true);
        bullet.setSceneView(assetLoader.loadTexture("tank_bullet.png"));
        bullet.addControl(new ProjectileControl(new Point2D(0, owner.isType(Type.PLAYER) ? -1 : 1), 10));
        bullet.addComponent(new OwnerComponent(owner));

        return bullet;
    }

    public static Entity newExplosion(Point2D position) {
        Entity explosion = Entity.noType();
        explosion.setPosition(position.subtract(40, 40));

        Texture animation = textureExplosion.toStaticAnimatedTexture(48, Duration.seconds(2));
        animation.setFitWidth(80);
        animation.setFitHeight(80);
        explosion.setSceneView(animation);
        explosion.setExpireTime(Duration.seconds(2));

        return explosion;
    }
}
