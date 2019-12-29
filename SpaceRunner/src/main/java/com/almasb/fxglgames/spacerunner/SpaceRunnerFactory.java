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

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.*;
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
import com.almasb.fxglgames.spacerunner.ai.AIPointComponent;
import com.almasb.fxglgames.spacerunner.ai.SquadAIComponent;
import com.almasb.fxglgames.spacerunner.components.EnemyComponent;
import com.almasb.fxglgames.spacerunner.components.MoveComponent;
import com.almasb.fxglgames.spacerunner.components.PlayerComponent;
import com.almasb.fxglgames.spacerunner.components.PowerupComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerFactory implements EntityFactory {

    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setStartColor(Color.WHITE);
        emitter.setEndColor(Color.YELLOW);
        emitter.setSize(1, 3);
        emitter.setNumParticles(25);
        emitter.setEmissionRate(1.0);
        emitter.setExpireFunction(i -> Duration.seconds(0.2));
        emitter.setSpawnPointFunction(i -> new Point2D(4, 17 + FXGLMath.random(-5, 5)));
        emitter.setVelocityFunction(i -> new Point2D(FXGLMath.random(360, 400), FXGLMath.random(0.0, 1.0)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);

        return entityBuilder()
                .type(SpaceRunnerType.PLAYER)
                .from(data)
                .viewWithBBox(texture("sprite_player.png", 40, 40))
                .with(new CollidableComponent(true), new ParticleComponent(emitter))
                .with(new HealthIntComponent(20))
                .with(new PlayerComponent(), new EffectComponent(), new KeepOnScreenComponent().onlyVertically())
                .build();
    }

    @Spawns("EnemyBullet")
    public Entity newEnemyBullet(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return entityBuilder()
                .type(SpaceRunnerType.ENEMY_BULLET)
                .from(data)
                .viewWithBBox(texture("sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(-1, 0), 350),
                        new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return entityBuilder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewWithBBox(texture("sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(1, 0), 1550),
                        new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Laser")
    public Entity newLaser(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return entityBuilder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewWithBBox(texture("sprite_laser.png"))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(1, 0), 850),
                        new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Rocket")
    public Entity newRocket(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return entityBuilder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewWithBBox(texture("rocket.png", 30, 8))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(1, 0), 750),
                        new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Enemy1")
    public Entity newEnemy(SpawnData data) {
        return entityBuilder()
                .type(SpaceRunnerType.ENEMY)
                .from(data)
                .viewWithBBox(texture("sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true), new MoveComponent())
                .with(new HealthIntComponent(10))
                .with(new EnemyComponent(), new KeepOnScreenComponent().onlyVertically())
                .with(new SquadAIComponent())
                .build();
    }

    @Spawns("Explosion")
    public Entity newExplosion(SpawnData data) {
        play("explosion.wav");

        return entityBuilder()
                .at(data.getX() - 40, data.getY() - 40)
                // we want a smaller texture, 80x80
                // it has 16 frames, hence 80 * 16
                .view(texture("explosion.png", 80 * 16, 80).toAnimatedTexture(16, Duration.seconds(0.5)).play())
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                .build();
    }

    @Spawns("powerup")
    public Entity newPowerup(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(SpaceRunnerType.POWERUP)
                .viewWithBBox("powerups/" + data.<PowerupType>get("type").getTextureName())
                .with(new CollidableComponent(true), new PowerupComponent(data.get("type")), new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }

    @Spawns("ai_point")
    public Entity newAIPoint(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(SpaceRunnerType.AI_POINT)
                .bbox(new HitBox("MAIN", new Point2D(-400, 0), BoundingShape.box(400, 30)))
                .view(new Text())
                .with("collisions", new ArrayList<Entity>())
                .with("enemies", 0)
                .with(new AIPointComponent())
                .with(new CollidableComponent(true), new MoveComponent())
                .build();
    }
}
