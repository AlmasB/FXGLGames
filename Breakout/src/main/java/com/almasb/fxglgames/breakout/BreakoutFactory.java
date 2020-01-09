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
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import com.almasb.fxglgames.breakout.components.MoveDownComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutFactory implements EntityFactory {

    private LazyValue<Image> image;

    public BreakoutFactory() {
        image = new LazyValue<>(() -> {
            var images = IntStream.rangeClosed(1, 8)
                    .mapToObj(i -> image("anim/Attack (" + i + ").png"))
                    .collect(Collectors.toList());

            return ImagesKt.merge(images);
        });

        image.get();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        var color = Color.valueOf(data.<String>get("color").toUpperCase());

        return entityBuilder()
                .from(data)
                .type(BreakoutType.BRICK)
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

        return entityBuilder()
                .from(data)
                .type(BreakoutType.BAT)
                .at(getAppWidth() / 2 - 50, getAppHeight() - 70)
                .viewWithBBox(getAssetLoader().loadTexture("bat.png", 464 / 3, 102 / 3))
                .collidable()
                .with(physics)
                .with(new BatComponent())
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

        var emitter = ParticleEmitters.newFireEmitter();
        emitter.setSourceImage(texture("ball.png"));
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setNumParticles(1);
        emitter.setEmissionRate(1);
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
        emitter.setScaleFunction(i -> new Point2D(-0.1, -0.1));
        emitter.setExpireFunction(i -> Duration.millis(110));

        var e = entityBuilder()
                .from(data)
                .type(BreakoutType.BALL)
                .bbox(new HitBox(BoundingShape.circle(64)))
                .view("ball.png")
                .collidable()
                .with(physics)
                .with(new TimeComponent())
                .with(new ParticleComponent(emitter))
                .with(new EffectComponent())
                .with(new BallComponent())
                .scale(0.1, 0.1)
                .build();

        emitter.setEntityScaleFunction(() -> new Point2D(0.1, 0.1));
        emitter.setScaleOriginFunction(i -> new Point2D(0, 0));
        e.getTransformComponent().setScaleOrigin(new Point2D(0, 0));

        emitter.minSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));
        emitter.maxSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));

        return e;
    }

    @Spawns("bulletBall")
    public Entity newBulletBall(SpawnData data) {
        Point2D dir = data.get("dir");

        return entityBuilder()
                .from(data)
                .type(BreakoutType.BULLET_BALL)
                .bbox(new HitBox(BoundingShape.circle(64)))
                .view("ball.png")
                .collidable()
                .with(new ProjectileComponent(dir, 800))
                .scale(0.1, 0.1)
                .build();
    }

    @Spawns("sparks")
    public Entity newSparks(SpawnData data) {
        var emitter = ParticleEmitters.newExplosionEmitter(14);
        emitter.setMaxEmissions(1);
        emitter.setStartColor(Color.WHITE);

        Color color = data.get("color");

        emitter.setEndColor(color);
        emitter.setSize(2, 8);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setNumParticles(20);

        return entityBuilder()
                .from(data)
                .with(new ParticleComponent(emitter))
                .with(new ExpireCleanComponent(Duration.seconds(1.5)))
                .build();
    }

    @Spawns("powerup")
    public Entity newPowerupGrow(SpawnData data) {
        var powerupType = FXGLMath.random(PowerupType.values()).get();

        var view = getUIFactory().newText(powerupType.toString(), Color.WHITE, 16);
        view.setFont(getUIFactory().newFont(FontType.GAME, 16));

        return entityBuilder()
                .from(data)
                .type(BreakoutType.POWERUP)
                .viewWithBBox(view)
                .collidable()
                .with(new MoveDownComponent(400))
                .with("powerupType", powerupType)
                .build();
    }

    @Spawns("zombie")
    public Entity newZombie(SpawnData data) {
        var channel = new AnimationChannel(image.get(), Duration.seconds(1), 8);

        return entityBuilder()
                .from(data)
                .view(new AnimatedTexture(channel).play())
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                .scale(0.2, 0.2)
                .scaleOrigin(0, 0)
                .build();
    }
}
