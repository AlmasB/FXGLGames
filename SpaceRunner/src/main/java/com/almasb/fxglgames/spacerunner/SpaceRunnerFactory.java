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

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.UserDataComponent;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.entity.control.KeepOnScreenControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxglgames.spacerunner.control.EnemyControl;
import com.almasb.fxglgames.spacerunner.control.PlayerControl;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerFactory implements EntityFactory {

    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.PLAYER)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_player.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new EffectControl(), new KeepOnScreenControl(false, true))
                .build();
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return Entities.builder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(new Point2D(1, 0), 1550),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Laser")
    public Entity newLaser(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return Entities.builder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_laser.png"))
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(new Point2D(1, 0), 850),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Rocket")
    public Entity newRocket(SpawnData data) {
        play("shoot" + FXGLMath.random(1, 4) + ".wav");

        return Entities.builder()
                .type(SpaceRunnerType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("rocket.png", 30, 8))
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(new Point2D(1, 0), 750),
                        new OffscreenCleanControl())
                .build();
    }

    @Spawns("Enemy1")
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .type(SpaceRunnerType.ENEMY)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true))
                .with(new EnemyControl(), new KeepOnScreenControl(false, true))
                .build();
    }
}
