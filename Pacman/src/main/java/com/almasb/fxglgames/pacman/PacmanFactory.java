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

package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxglgames.pacman.components.PaletteChangingComponent;
import com.almasb.fxglgames.pacman.components.PlayerComponent;
import com.almasb.fxglgames.pacman.components.ai.DelayedChasePlayerComponent;
import com.almasb.fxglgames.pacman.components.ai.GuardCoinComponent;
import com.almasb.fxglgames.pacman.components.ai.RandomAStarMoveComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.pacman.PacmanApp.BLOCK_SIZE;
import static com.almasb.fxglgames.pacman.PacmanType.*;

/**
 * Factory for creating in-game entities.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PacmanFactory implements EntityFactory {

    @Spawns("1")
    public Entity newBlock(SpawnData data) {
        var rect = new Rectangle(38, 38, Color.BLACK);
        rect.setArcWidth(25);
        rect.setArcHeight(25);
        rect.setStrokeWidth(1);
        rect.setStroke(Color.BLUE);

        return entityBuilder(data)
                .type(BLOCK)
                .viewWithBBox(rect)
                .zIndex(-1)
                .build();
    }

    @Spawns("0")
    public Entity newCoin(SpawnData data) {
        var view = texture("coin.png");
        view.setTranslateX(5);
        view.setTranslateY(5);

        return entityBuilder(data)
                .type(COIN)
                .bbox(new HitBox(new Point2D(5, 5), BoundingShape.box(30, 30)))
                .view(view)
                .zIndex(-1)
                .with(new CollidableComponent(true))
                .with(new CellMoveComponent(BLOCK_SIZE, BLOCK_SIZE, 50))
                .scale(0.5, 0.5)
                .build();
    }

    @Spawns("P")
    public Entity newPlayer(SpawnData data) {
        AnimatedTexture view = texture("player.png").toAnimatedTexture(2, Duration.seconds(0.33));

        var e = entityBuilder(data)
                .type(PLAYER)
                .bbox(new HitBox(new Point2D(4, 4), BoundingShape.box(32, 32)))
                .view(view.loop())
                .with(new CollidableComponent(true))
                .with(new CellMoveComponent(BLOCK_SIZE, BLOCK_SIZE, 200).allowRotation(true))
                // there is no grid constructed yet, so pass lazily
                .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                .with(new PlayerComponent())
                .rotationOrigin(35 / 2.0, 40 / 2.0)
                .build();

        e.setLocalAnchorFromCenter();

        return e;
    }

    private Supplier<Component> aiComponents = new Supplier<>() {
        private Map<Integer, Supplier<Component>> components = Map.of(
                0, () -> new DelayedChasePlayerComponent().withDelay(),
                1, GuardCoinComponent::new,
                2, RandomAStarMoveComponent::new,
                3, DelayedChasePlayerComponent::new
        );

        private int index = 0;

        @Override
        public Component get() {
            // there are 4 enemies
            if (index == 4) {
                index = 0;
            }

            return components.get(index++).get();
        }
    };

    @Spawns("E")
    public Entity newEnemy(SpawnData data) {
        Entity enemy = entityBuilder(data)
                .type(ENEMY)
                .bbox(new HitBox(new Point2D(2, 2), BoundingShape.box(36, 36)))
                .with(new CollidableComponent(true))
                .with(new PaletteChangingComponent(texture("spritesheet.png")))
                .with(new CellMoveComponent(BLOCK_SIZE, BLOCK_SIZE, 125))
                // there is no grid constructed yet, so pass lazily
                .with(new AStarMoveComponent(new LazyValue<>(() -> geto("grid"))))
                .with(aiComponents.get())
                .scaleOrigin(0, 0)
                .scale(0.24, 0.24)
                .build();

        enemy.setLocalAnchorFromCenter();

        return enemy;
    }
}
