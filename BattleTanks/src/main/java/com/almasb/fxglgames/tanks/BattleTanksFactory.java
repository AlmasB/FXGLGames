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

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxglgames.tanks.Config.BULLET_SPEED;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BattleTanksFactory implements EntityFactory {

    @Spawns("player,playerSpawnPoint")
    public Entity newPlayer(SpawnData data) {
        var e = entityBuilder()
                .from(data)
                .type(BattleTanksType.PLAYER)
                .bbox(new HitBox(new Point2D(10, 10), BoundingShape.box(64, 64)))
                .collidable()
                .with(new MoveComponent())
                .with(new TankViewComponent())
                .with(new PlayerArrowViewComponent())
                .scale(0.4, 0.4)
                .build();

        e.getTransformComponent().setScaleOrigin(new Point2D(42, 42));

        return e;
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(BattleTanksType.BULLET)
                //.bbox(new HitBox("BODY", new Point2D(34, 34), BoundingShape.box(16, 16)))
                .viewWithBBox("tank_bullet.png")
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanComponent(), new ProjectileComponent(data.get("direction"), BULLET_SPEED))
                .build();
    }

    @Spawns("playerFlag")
    public Entity newPlayerFlag(SpawnData data) {
        return newEnemyFlag(data);
    }

    @Spawns("enemyFlag")
    public Entity newEnemyFlag(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(BattleTanksType.FLAG)
                .viewWithBBox(texture("flag.png", data.<Integer>get("width"), data.<Integer>get("height")))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(BattleTanksType.WALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height")))
                .collidable()
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(BattleTanksType.BRICK)
                .viewWithBBox(texture("brick.png", data.<Integer>get("width"), data.<Integer>get("height")))
                .collidable()
                .build();
    }

    @Spawns("enemy,enemySpawnPoint")
    public Entity newEnemy(SpawnData data) {
        var e = entityBuilder()
                .from(data)
                .type(BattleTanksType.ENEMY)
                .bbox(new HitBox(new Point2D(10, 10), BoundingShape.box(64, 64)))
                .collidable()
                .with(new MoveComponent())
                .with(new TankViewComponent())
                .with(new RandomMoveComponent())
                .scale(0.4, 0.4)
                .build();

        e.getTransformComponent().setScaleOrigin(new Point2D(42, 42));

        return e;
    }
}
