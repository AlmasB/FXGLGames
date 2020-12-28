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

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.tanks.components.*;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxglgames.tanks.BattleTanksType.*;
import static com.almasb.fxglgames.tanks.Config.BLOCK_SIZE;
import static com.almasb.fxglgames.tanks.Config.BULLET_SPEED;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BattleTanksFactory implements EntityFactory {

    @Spawns("player,playerSpawnPoint")
    public Entity newPlayer(SpawnData data) {
        return newTank(data)
                .type(PLAYER)
                .with(new PlayerArrowViewComponent())
                .rotationOrigin(new Point2D(18, 18))
                .build();
    }

    @Spawns("enemy,enemySpawnPoint")
    public Entity newEnemy(SpawnData data) {
        var rect = new Rectangle(36, 36, Color.RED);
        rect.setBlendMode(BlendMode.COLOR_BURN);

        var e = newTank(data)
                .view(rect)
                .type(ENEMY)
                .rotationOrigin(new Point2D(18, 18))
                .build();

        e.setLocalAnchor(new Point2D(18, 18));

        return e;
    }

    @Spawns("ally,allySpawnPoint")
    public Entity newAlly(SpawnData data) {
        var e = newTank(data)
                .type(ALLY)
                .rotationOrigin(new Point2D(18, 18))
                .build();

        e.setLocalAnchor(new Point2D(18, 18));

        return e;
    }

    private EntityBuilder newTank(SpawnData data) {
        return entityBuilder(data)
                .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(26, 26)))
                //.anchorFromCenter()
                .collidable()
                .with(new HealthIntComponent(10))
                .with(new MoveComponent())
                .with(new TankViewComponent())
                .with(new CellMoveComponent(BLOCK_SIZE / 2, BLOCK_SIZE / 2, 300).allowRotation(true))
                .with(new ActionComponent())
                .with(new StateComponent())
                .with(new TankAIComponent());
    }

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        Entity owner = data.get("owner");

        var collidable = new CollidableComponent(true);
        collidable.addIgnoredType(owner.getType());
        collidable.addIgnoredType(owner.getType() == PLAYER || owner.getType() == ALLY ? PLAYER_FLAG : ENEMY_FLAG);

        if (owner.getType() == PLAYER) {
            collidable.addIgnoredType(ALLY);
        }

        if (owner.getType() == ALLY) {
            collidable.addIgnoredType(PLAYER);
        }

        return entityBuilder(data)
                .at(data.getX() - 8, data.getY() - 8)
                .type(BULLET)
                .viewWithBBox("tank_bullet.png")
                .scale(0.5, 0.5)
                .with(collidable)
                .with(new OffscreenCleanComponent())
                .with(new ProjectileComponent(data.get("direction"), BULLET_SPEED))
                .build();
    }

    @Spawns("playerFlag")
    public Entity newPlayerFlag(SpawnData data) {
        return newFlag(data)
                .type(PLAYER_FLAG)
                .build();
    }

    @Spawns("enemyFlag")
    public Entity newEnemyFlag(SpawnData data) {
        return newFlag(data)
                .type(ENEMY_FLAG)
                .build();
    }

    private EntityBuilder newFlag(SpawnData data) {
        return entityBuilder(data)
                .viewWithBBox(texture("flag.png", data.<Integer>get("width"), data.<Integer>get("height")))
                .collidable();
    }

    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        return entityBuilder(data)
                .type(WALL)
                .viewWithBBox(new Rectangle(data.<Integer>get("width"), data.<Integer>get("height")))
                .collidable()
                .build();
    }

    @Spawns("brick")
    public Entity newBrick(SpawnData data) {
        return entityBuilder(data)
                .type(BRICK)
                .viewWithBBox(texture("brick.png", data.<Integer>get("width"), data.<Integer>get("height")))
                .collidable()
                .with(new BrickComponent())
                .build();
    }
}
