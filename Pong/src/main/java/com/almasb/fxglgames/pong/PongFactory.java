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

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongFactory implements EntityFactory {

    @Spawns("ball")
    public Entity newBall(SpawnData data) {
        Entity ball = FXGL.entityBuilder()
                .from(data)
                .type(EntityType.BALL)
                .bbox(new HitBox(BoundingShape.circle(5)))
                .build();

        PhysicsComponent ballPhysics = new PhysicsComponent();
        ballPhysics.setBodyType(BodyType.DYNAMIC);

        FixtureDef def = new FixtureDef().density(0.3f).restitution(1.0f);

        ballPhysics.setFixtureDef(def);
        ballPhysics.setOnPhysicsInitialized(() -> ballPhysics.setLinearVelocity(5 * 60, -5 * 60));

        ball.addComponent(ballPhysics);
        ball.addComponent(new CollidableComponent(true));
        ball.addComponent(new BallComponent());

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setStartColor(Color.LIGHTYELLOW);
        emitter.setEndColor(Color.RED);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(5, 10);
        emitter.setEmissionRate(1);

        ball.addComponent(new ParticleComponent(emitter));

        return ball;
    }

    @Spawns("bat")
    public Entity newBat(SpawnData data) {
        boolean isPlayer = data.get("isPlayer");

        Entity bat = FXGL.entityBuilder()
                .from(data)
                .type(isPlayer ? EntityType.PLAYER_BAT : EntityType.ENEMY_BAT)
                .viewWithBBox(new Rectangle(20, 60, Color.LIGHTGRAY))
                .with(new CollidableComponent(true))
                .build();

        PhysicsComponent batPhysics = new PhysicsComponent();
        batPhysics.setBodyType(BodyType.KINEMATIC);
        bat.addComponent(batPhysics);

        bat.addComponent(isPlayer ? new BatComponent() : new EnemyBatComponent());

        return bat;
    }
}
