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

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.extra.entity.components.ExpireCleanControl;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.extra.entity.components.OffscreenCleanControl;
import com.almasb.fxgl.extra.entity.components.ProjectileControl;
import com.almasb.fxgl.extra.entity.effect.EffectComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spaceinvaders.component.InvincibleComponent;
import com.almasb.fxglgames.spaceinvaders.component.OwnerComponent;
import com.almasb.fxglgames.spaceinvaders.component.SubTypeComponent;
import com.almasb.fxglgames.spaceinvaders.control.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

import static com.almasb.fxgl.app.DSLKt.*;
import static com.almasb.fxglgames.spaceinvaders.Config.LEVEL_START_DELAY;
import static com.almasb.fxglgames.spaceinvaders.SpaceInvadersType.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class SpaceInvadersFactory implements EntityFactory {

    private static final Random random = FXGLMath.getRandom();

    private static final RenderLayer METEORS = new RenderLayer() {
        @Override
        public String name() {
            return "METEORS";
        }

        @Override
        public int index() {
            return 1001;
        }
    };

    private static final int NUM_STARS = 70;

    @Spawns("Background")
    public Entity newBackground(SpawnData data) {
        return Entities.builder()
                .viewFromNode(texture("background/background.png", Config.WIDTH, Config.HEIGHT))
                .renderLayer(RenderLayer.BACKGROUND)
                .build();
    }

    @Spawns("Stars")
    public Entity newStars(SpawnData data) {
        Group group = new Group();

        for (int i = 0; i < NUM_STARS; i++) {
            group.getChildren().addAll(new Rectangle());
        }

        EntityView view = new EntityView(group);

        return Entities.builder()
                .viewFromNode(view)
                .renderLayer(RenderLayer.BACKGROUND)
                .with(new StarsControl())
                .build();
    }

    @Spawns("Meteor")
    public Entity newMeteor(SpawnData data) {
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

            y = random.nextInt((int) h);
        } else {
            // top or bot
            if (random.nextBoolean()) {
                y = -50;
            } else {
                y = h + 50;
            }

            x = random.nextInt((int) w);
        }

        Entity meteor = Entities.builder()
                .at(x, y)
                .viewFromTexture("background/meteor" + FXGLMath.random(1, 4) + ".png")
                .renderLayer(METEORS)
                .with(new MeteorControl())
                .build();

        // add offscreen clean a bit later so that they are not cleaned from start
        FXGL.getMasterTimer()
                .runOnceAfter(() -> {
                    meteor.addComponent(new OffscreenCleanControl());
                }, Duration.seconds(5));

        return meteor;
    }

    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        Texture texture = texture("player2.png");
        texture.setPreserveRatio(true);
        texture.setFitHeight(40);

        return Entities.builder()
                .from(data)
                .type(PLAYER)
                .viewFromNodeWithBBox(texture)
                .with(new CollidableComponent(true), new InvincibleComponent())
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Enemy")
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ENEMY)
                .viewFromNodeWithBBox(
                        texture("enemy" + ((int)(Math.random() * 3) + 1) + ".png").toAnimatedTexture(2, Duration.seconds(2))
                )
                .with(new CollidableComponent(true), new HealthComponent(2), new TimeComponent(1.0))
                .with(new EnemyControl(), new EffectComponent())
                .build();
    }

    @Spawns("Boss")
    public Entity newBoss(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ENEMY)
                .viewFromTextureWithBBox("boss.png")
                .with(new CollidableComponent(true), new HealthComponent(50))
                .with(new BossControl())
                .build();
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        Entity owner = data.get("owner");

        return Entities.builder()
                .type(BULLET)
                .at(owner.getCenter().add(-8, 20))
                .viewFromTextureWithBBox("enemy_bullet.png")
                .with(new CollidableComponent(true), new OwnerComponent(owner.getType()))
                .with(new ProjectileControl(new Point2D(0, 1), 600), new OffscreenCleanControl())
                .with("dead", false)
                .build();
    }

    @Spawns("Laser")
    public Entity newLaser(SpawnData data) {
        Entity owner = data.get("owner");

        Entity bullet = Entities.builder()
                .type(BULLET)
                .at(owner.getCenter().add(-4.5, -20))
                .bbox(new HitBox(BoundingShape.box(9, 20)))
                .with(new CollidableComponent(true), new OwnerComponent(owner.getType()))
                .with(new OffscreenCleanControl(), new BulletControl(850))
                .build();

        Texture t = texture("laser2.png");
        t.relocate(-2, -20);

        EntityView view = new EntityView();
        view.addNode(t);
        view.setEffect(new Bloom(0.5));

        bullet.setView(view);

        return bullet;
    }

    @Spawns("LaserHit")
    public Entity newLaserHit(SpawnData data) {
        return Entities.builder()
                .at(data.getX() - 15, data.getY() - 15)
                .viewFromNode(texture("laser_hit.png", 15, 15))
                .with(new LaserHitControl())
                .build();
    }

    @Spawns("LaserBeam")
    public Entity newLaserBeam(SpawnData data) {
        Rectangle view = new Rectangle(10, Config.HEIGHT - 25, Color.color(1.0, 1.0, 1.0, 0.86));
        view.setArcWidth(15);
        view.setArcHeight(15);
        view.setStroke(Color.BLUE);
        view.setStrokeWidth(1);

        return Entities.builder()
                .from(data)
                .type(LASER_BEAM)
                .viewFromNodeWithBBox(view)
                .with(new CollidableComponent(true))
                .with(new LaserBeamControl())
                .build();
    }

    @Spawns("Wall")
    public Entity newWall(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(WALL)
                .viewFromNodeWithBBox(texture("wall.png", 232 / 3, 104 / 3))
                .with(new CollidableComponent(true))
                .with(new WallControl(7))
                .build();
    }

    @Spawns("Bonus")
    public Entity newBonus(SpawnData data) {
        BonusType type = data.get("type");

        return Entities.builder()
                .from(data)
                .type(BONUS)
                .viewFromTextureWithBBox(type.textureName)
                .with(new SubTypeComponent(type), new CollidableComponent(true))
                .with(new BonusControl())
                .build();
    }

    @Spawns("Explosion")
    public Entity newExplosion(SpawnData data) {
        play("explosion.wav");

        return Entities.builder()
                .at(data.getX() - 40, data.getY() - 40)
                // we want a smaller texture, 80x80
                // it has 16 frames, hence 80 * 16
                .viewFromAnimatedTexture(texture("explosion.png", 80 * 16, 80).toAnimatedTexture(16, Duration.seconds(0.5)), false, true)
                .build();
    }

    @Spawns("LevelInfo")
    public Entity newLevelInfo(SpawnData data) {
        Text levelText = FXGL.getUIFactory().newText("Level " + geti("level"), Color.AQUAMARINE, 44);

        Entity levelInfo = Entities.builder()
                .viewFromNode(levelText)
                .with(new ExpireCleanControl(Duration.seconds(LEVEL_START_DELAY)))
                .build();

        Entities.animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(LEVEL_START_DELAY - 0.1))
                .translate(levelInfo)
                .from(new Point2D(FXGL.getAppWidth() / 2 - levelText.getLayoutBounds().getWidth() / 2, 0))
                .to(new Point2D(FXGL.getAppWidth() / 2 - levelText.getLayoutBounds().getWidth() / 2, FXGL.getAppHeight() / 2))
                .buildAndPlay();

        return levelInfo;
    }
}
