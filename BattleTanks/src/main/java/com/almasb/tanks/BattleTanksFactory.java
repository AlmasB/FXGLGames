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

package com.almasb.tanks;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;

import static com.almasb.tanks.Config.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@SetEntityFactory
public class BattleTanksFactory implements TextEntityFactory {

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(BattleTanksType.BULLET)
                .bbox(new HitBox("BODY", new Point2D(34, 34), BoundingShape.box(16, 16)))
                .viewFromTexture("bullet.png")
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl(), new ProjectileControl(data.get("direction"), BULLET_SPEED))
                .build();
    }

    @Spawns("Flag")
    @SpawnSymbol('F')
    public Entity newFlag(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(BattleTanksType.FLAG)
                .viewFromTextureWithBBox("flag.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("Wall")
    @SpawnSymbol('1')
    public Entity newWall(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(BattleTanksType.WALL)
                .viewFromTextureWithBBox("wall.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @Override
    public char emptyChar() {
        return '0';
    }

    @Override
    public int blockWidth() {
        return 84;
    }

    @Override
    public int blockHeight() {
        return 84;
    }
}
