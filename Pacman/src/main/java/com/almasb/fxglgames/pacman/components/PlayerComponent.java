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

package com.almasb.fxglgames.pacman.components;

import com.almasb.fxgl.core.util.LazyValue;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import static com.almasb.fxglgames.pacman.components.MoveDirection.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private CellMoveComponent moveComponent;

    private LazyValue<AStarMoveComponent> astar = new LazyValue<>(() -> {
        return entity.getComponent(AStarMoveComponent.class);
    });

    private MoveDirection currentMoveDir = RIGHT;
    private MoveDirection nextMoveDir = RIGHT;

    public void up() {
        nextMoveDir = UP;
    }

    public void down() {
        nextMoveDir = DOWN;
    }

    public void left() {
        nextMoveDir = LEFT;
    }

    public void right() {
        nextMoveDir = RIGHT;
    }

    @Override
    public void onUpdate(double tpf) {
        var x = moveComponent.getCellX();
        var y = moveComponent.getCellY();

//        if (x == 0 && currentMoveDir == LEFT) {
//
//            // TODO: moveComponent set to cell?
//            // TODO: astar cancel movement
//            entity.setX((astar.get().getGrid().getWidth() - 1) * BLOCK_SIZE);
//            return;
//
//        } else if (x == astar.get().getGrid().getWidth() - 1 && currentMoveDir == RIGHT) {
//            entity.setX(0);
//            return;
//        }

        if (astar.get().isMoving())
            return;

        switch (nextMoveDir) {
            case UP:
                if (astar.get().getGrid().getUp(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case RIGHT:
                if (astar.get().getGrid().getRight(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case DOWN:
                if (astar.get().getGrid().getDown(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case LEFT:
                if (astar.get().getGrid().getLeft(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
        }

        switch (currentMoveDir) {
            case UP:
                astar.get().moveToUpCell();
                break;
            case RIGHT:
                astar.get().moveToRightCell();
                break;
            case DOWN:
                astar.get().moveToDownCell();
                break;
            case LEFT:
                astar.get().moveToLeftCell();
                break;
        }
    }


//
//    public void teleport() {
//        Random random = new Random();
//
//        AStarGrid grid = ((PacmanApp) FXGL.getApp()).getGrid();
//
//        int x, y;
//
//        do {
//            x = (random.nextInt(PacmanApp.MAP_SIZE - 2) + 1);
//            y = (random.nextInt(PacmanApp.MAP_SIZE - 2) + 1);
//        } while (grid.getNodeState(x, y) != NodeState.WALKABLE);
//
//        position.setValue(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE);
//
//        playFadeAnimation();
//    }
//
//    private void playFadeAnimation() {
//        FadeTransition ft = new FadeTransition(Duration.seconds(0.5), view.getView());
//        ft.setFromValue(1);
//        ft.setToValue(0);
//        ft.setAutoReverse(true);
//        ft.setCycleCount(2);
//        ft.play();
//    }
//
//    private List<Entity> blocks;
//
//    private void move(double dx, double dy) {
//        if (!getEntity().isActive())
//            return;
//
//        if (blocks == null) {
//            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.BLOCK);
//        }
//
//        double mag = Math.sqrt(dx * dx + dy * dy);
//        long length = Math.round(mag);
//
//        double unitX = dx / mag;
//        double unitY = dy / mag;
//
//        for (int i = 0; i < length; i++) {
//            position.translate(unitX, unitY);
//
//            boolean collision = false;
//
//            for (int j = 0; j < blocks.size(); j++) {
//                if (blocks.get(j).getBoundingBoxComponent().isCollidingWith(bbox)) {
//                    collision = true;
//                    break;
//                }
//            }
//
//            if (collision) {
//                position.translate(-unitX, -unitY);
//                break;
//            }
//        }
//    }
}
