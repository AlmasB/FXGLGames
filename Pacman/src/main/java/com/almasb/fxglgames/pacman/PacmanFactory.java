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

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.pacman.control.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Factory for creating in-game entities.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PacmanFactory implements EntityFactory {

    @Spawns("1")
    public Entity newBlock(SpawnData data) {
        return entityBuilder()
                .from(data)
                .type(PacmanType.BLOCK)
                .viewWithBBox(new Rectangle(40, 40))
                .zIndex(-1)
                .build();
    }

    @Spawns("0")
    public Entity newCoin(SpawnData data) {
        var view = texture("coin.png");
        view.setTranslateX(2.5);

        return entityBuilder()
                .from(data)
                .type(PacmanType.COIN)
                .bbox(new HitBox("Main", BoundingShape.box(40, 40)))
                .viewWithBBox(view)
                .zIndex(-1)
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("P")
    public Entity newPlayer(SpawnData data) {
        Texture view = texture("player.png").toAnimatedTexture(2, Duration.seconds(0.33));

        return entityBuilder()
                .from(data)
                .type(PacmanType.PLAYER)
                .bbox(new HitBox(new Point2D(2, 2), BoundingShape.box(36, 36)))
                .view(view)
                .with(new CollidableComponent(true))
                .with(new PlayerComponent())
                .build();
    }

    private Supplier<String> trees = new Supplier<String>() {
        private int index = 0;

        private List<String> names = Arrays.asList("guard.tree", "astar.tree", "chaser.tree", "random.tree");

        @Override
        public String get() {
            if (index == names.size()) {
                index = 0;
            }

            return names.get(index++);
        }
    };

    @Spawns("E")
    public Entity newEnemy(SpawnData data) {
        String aiName = trees.get();

        Entity enemy = entityBuilder()
                .from(data)
                .type(PacmanType.ENEMY)
                .bbox(new HitBox(new Point2D(2, 2), BoundingShape.box(36, 36)))
                .with(new CollidableComponent(true))
                //.with(new AIControl(aiName), new MoveControl(), new AStarMoveControl(), new PaletteChangingControl(texture("spritesheet.png")))
                .build();

        if (aiName.equals("guard.tree")) {
            //enemy.removeComponent(MoveControl.class);
        }

        return enemy;
    }
}
