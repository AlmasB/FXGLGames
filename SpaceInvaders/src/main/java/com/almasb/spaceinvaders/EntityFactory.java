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
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.spaceinvaders.component.HPComponent;
import com.almasb.spaceinvaders.component.InvincibleComponent;
import com.almasb.spaceinvaders.component.OwnerComponent;
import com.almasb.spaceinvaders.component.SubTypeComponent;
import com.almasb.spaceinvaders.control.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class EntityFactory {

    public enum EntityType {
        PLAYER, ENEMY, BULLET, WALL, BONUS
    }

    public enum BonusType {
        ATTACK_RATE("powerup_atk_rate.png"), LIFE("life.png");

        final String textureName;

        BonusType(String textureName) {
            this.textureName = textureName;
        }
    }

    private enum RenderLayer implements com.almasb.fxgl.entity.RenderLayer {
        BACKGROUND(100),
        METEORS(200);

        private final int index;

        RenderLayer(int index) {
            this.index = index;
        }

        @Override
        public int index() {
            return index;
        }
    }

    private static final AssetLoader assetLoader = FXGL.getAssetLoader();

    private static final Random random = new Random();

    public static Entity newBackground(double w, double h) {
        GameEntity bg = Entities.builder()
                .viewFromNode(assetLoader.loadTexture("background/background.png", w, h))
                .build();

        bg.getMainViewComponent().setRenderLayer(RenderLayer.BACKGROUND);

        return bg;
    }

    public static Entity newMeteor() {
        double w = FXGL.getSettings().getWidth();
        double h = FXGL.getSettings().getHeight();
        double x = 0, y = 0;

        // these are deliberately arbitrary to create illusion of randomness
        if (random.nextBoolean()) {
            // left or right
            if (random.nextBoolean()) {
                x = -50;
            } else {
                x = w + 50;
            }

            y = random.nextInt((int)h);
        } else {
            // top or bot
            if (random.nextBoolean()) {
                y = -50;
            } else {
                y = h + 50;
            }

            x = random.nextInt((int) w);
        }

        GameEntity meteor = new GameEntity();
        meteor.getPositionComponent().setValue(x, y);

        String textureName = "background/meteor" + (random.nextInt(4) + 1) + ".png";

        meteor.getMainViewComponent().setTexture(textureName);
        meteor.getMainViewComponent().setRenderLayer(RenderLayer.METEORS);

        meteor.addControl(new MeteorControl());

        // add offscreen clean a bit later so that they are not cleaned from start
        FXGL.getMasterTimer()
                .runOnceAfter(() -> {
                    meteor.addControl(new OffscreenCleanControl());
                }, Duration.seconds(5));

        return meteor;
    }

    public static GameEntity newPlayer(double x, double y) {
        GameEntity player = new GameEntity();
        player.getTypeComponent().setValue(EntityType.PLAYER);
        player.getPositionComponent().setValue(x, y);

        Texture texture = assetLoader.loadTexture("player2.png");
        texture.setPreserveRatio(true);
        texture.setFitHeight(40);

        player.getMainViewComponent().setView(new EntityView(texture), true);

        player.addComponent(new CollidableComponent(true));
        player.addComponent(new InvincibleComponent());
        player.addControl(new PlayerControl());

        return player;
    }

    public static Entity newEnemy(double x, double y) {
        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(x, y)
                .viewFromNodeWithBBox(assetLoader
                        .loadTexture("enemy" + ((int)(Math.random() * 3) + 1) + ".png")
                        .toAnimatedTexture(2, Duration.seconds(2)))
                .with(new CollidableComponent(true), new HPComponent(2))
                .with(new EnemyControl())
                .build();
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
        bullet.addControl(new ProjectileControl(new Point2D(0, Entities.getType(owner).isType(EntityType.PLAYER) ? -1 : 1), 10 * 60));
        bullet.addComponent(new OwnerComponent(Entities.getType(owner).getValue()));
        bullet.addControl(new OffscreenCleanControl());

        bullet.setProperty("dead", false);

        return bullet;
    }

    public static Entity newLaser(Entity owner) {
        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(EntityType.BULLET);

        Point2D center = Entities.getBBox(owner)
                .getCenterWorld()
                .add(-4.5, -20);

        bullet.getPositionComponent().setValue(center);

        bullet.getBoundingBoxComponent().addHitBox(new HitBox("HIT", BoundingShape.box(9, 20)));
        bullet.addComponent(new CollidableComponent(true));
        bullet.addComponent(new OwnerComponent(Entities.getType(owner).getValue()));
        bullet.addControl(new OffscreenCleanControl());
        bullet.addControl(new BulletControl(500));

        DropShadow shadow = new DropShadow(22, Color.DARKBLUE);
        shadow.setInput(new Glow(0.8));

        EntityView view = new EntityView();
        view.addNode(assetLoader.loadTexture("laser1.png"));

        Texture t = assetLoader.loadTexture("laser2.png");
        t.relocate(-2, -20);

        view.addNode(t);
        view.setEffect(shadow);

        bullet.getMainViewComponent().setView(view);

        return bullet;
    }

    public static Entity newWall(double x, double y) {
        return Entities.builder()
                .type(EntityType.WALL)
                .at(x, y)
                .viewFromTextureWithBBox("wall.png")
                .with(new CollidableComponent(true), new HPComponent(7))
                .build();
    }

    public static Entity newBonus(double x, double y, BonusType type) {
        return Entities.builder()
                .type(EntityType.BONUS)
                .at(x, y)
                .viewFromTextureWithBBox(type.textureName)
                .with(new SubTypeComponent(type), new CollidableComponent(true))
                .with(new BonusControl())
                .build();
    }

    public static Entity newExplosion(Point2D position) {
        GameEntity explosion = Entities.builder()
                .at(position.subtract(40, 40))
                // texture is 256x256, we want smaller, 80x80
                // it has 48 frames, hence 80 * 48
                .viewFromNode(assetLoader.loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
                .with(new ExpireCleanControl(Duration.seconds(1.8)))
                .build();

        // slightly better looking effect
        explosion.getView().setBlendMode(BlendMode.ADD);

        return explosion;
    }

    public static Entity newLaserHit(Point2D position) {
        return Entities.builder()
                .at(position.subtract(15, 15))
                .viewFromNode(assetLoader.loadTexture("laser_hit.png", 15, 15))
                .with(new LaserHitControl())
                .build();
    }
}
