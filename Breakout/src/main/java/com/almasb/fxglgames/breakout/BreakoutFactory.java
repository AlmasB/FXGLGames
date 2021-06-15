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

package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxglgames.breakout.components.BackgroundStarsViewComponent;
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.breakout.BreakoutType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutFactory implements EntityFactory {

    private LazyValue<Image> image;

    public BreakoutFactory() {
        image = new LazyValue<>(() -> {
            var images = IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> image("anim/Attack_" + i + ".png"))
                    .collect(Collectors.toList());

            return ImagesKt.merge(images);
        });

        image.get();
    }

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
                .type(BACKGROUND)
                .with(new IrremovableComponent())
                .with(new BackgroundStarsViewComponent(
                        texture("bg/bg_blue.png"),
                        texture("bg/stars_small_1.png"),
                        texture("bg/stars_big_1.png")
                ))
                .build();
    }

    @Spawns("colorCircle")
    public Entity newColorCircle(SpawnData data) {
        var radius = 200;

        return entityBuilder(data)
                .type(COLOR_CIRCLE)
                .bbox(new HitBox(BoundingShape.circle(radius)))
                //.view(circle)
                .with(new PhysicsComponent())
                .with(new IrremovableComponent())
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        var color = Color.valueOf(data.<String>get("color").toUpperCase());

        return entityBuilder(data)
                .type(BRICK)
                .bbox(new HitBox(BoundingShape.box(96, 32)))
                .collidable()
                .with(new PhysicsComponent())
                .with(new BrickComponent(color))
                .build();
    }

    @Spawns("bat")
    public Entity newBat(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return entityBuilder(data)
                .type(BAT)
                .at(getAppWidth() / 2.0 - 50, getAppHeight() - 70)
                .viewWithBBox(texture("bat.png", 464 / 3.0, 102 / 3.0))
                .scaleOrigin(464 / 3.0 / 2, 0)
                .collidable()
                .with(physics)
                .with(new EffectComponent())
                .with(new BatComponent(texture("bat_hit.png", 464 / 3.0, 102 / 3.0)))
                .build();
    }

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().restitution(1f).density(0.03f));

        var bd = new BodyDef();
        bd.setType(BodyType.DYNAMIC);
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);

        var e = entityBuilder(data)
                .type(BALL)
                .bbox(new HitBox(BoundingShape.circle(64)))
                .view("ball.png")
                .collidable()
                .with(physics)
                .with(new TimeComponent())
                .with(new EffectComponent())
                .with(new BallComponent())
                .scaleOrigin(0, 0)
                .scale(0.1, 0.1)
                .build();

        // disable particle effects in native mode
        if (!getSettings().isNative()) {
            var emitter = ParticleEmitters.newFireEmitter();
            emitter.setSourceImage(texture("ball.png"));
            emitter.setBlendMode(BlendMode.SRC_OVER);
            emitter.setNumParticles(1);
            emitter.setEmissionRate(1);
            emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
            emitter.setScaleFunction(i -> new Point2D(-0.1, -0.1));
            emitter.setExpireFunction(i -> Duration.seconds(1));

            emitter.setEntityScaleFunction(() -> new Point2D(0.1, 0.1));
            emitter.setScaleOriginFunction(i -> new Point2D(0, 0));

            emitter.minSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));
            emitter.maxSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));

            e.addComponent(new ParticleComponent(emitter));
        }

        return e;
    }

    @Spawns("bulletBall")
    public Entity newBulletBall(SpawnData data) {
        Point2D dir = data.get("dir");

        return entityBuilder(data)
                .type(BULLET_BALL)
                .bbox(new HitBox(BoundingShape.circle(64)))
                .view("ball.png")
                .collidable()
                .with(new ProjectileComponent(dir, 800))
                .scale(0.1, 0.1)
                .build();
    }

    @Spawns("sparks")
    public Entity newSparks(SpawnData data) {
        Color color = data.get("color");

        var e = entityBuilder(data)
                .with(new ExpireCleanComponent(Duration.seconds(1.5)))
                .build();

        if (!getSettings().isNative()) {
            var emitter = ParticleEmitters.newExplosionEmitter(24);
            emitter.setSourceImage(texture("particles/smoke_06.png", 16, 16).multiplyColor(color));
            emitter.setSize(4, 16);
            emitter.setMaxEmissions(1);
            emitter.setExpireFunction(i -> Duration.seconds(FXGLMath.random(0.25, 1.0)));
            emitter.setBlendMode(BlendMode.ADD);
            emitter.setNumParticles(20);

            e.addComponent(new ParticleComponent(emitter));
        }

        return e;
    }

    @Spawns("powerup")
    public Entity newPowerupGrow(SpawnData data) {
        var powerupType = FXGLMath.random(PowerupType.values()).get();

        var view = getUIFactoryService().newText(powerupType.toString(), Color.WHITE, FontType.GAME, 16);

        return entityBuilder(data)
                .type(POWERUP)
                .viewWithBBox(view)
                .collidable()
                .with(new ProjectileComponent(new Point2D(0, 1), 400).allowRotation(false))
                .with("powerupType", powerupType)
                .build();
    }

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        var channel = new AnimationChannel(image.get(), Duration.seconds(1), 8);

        return entityBuilder(data)
                .view(new AnimatedTexture(channel).play())
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                .scale(0.2, 0.2)
                .scaleOrigin(0, 0)
                .build();
    }
}
