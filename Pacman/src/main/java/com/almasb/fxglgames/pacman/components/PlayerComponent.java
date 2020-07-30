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

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import static com.almasb.fxglgames.pacman.components.PlayerComponent.MoveDirection.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(AStarMoveComponent.class)
public class PlayerComponent extends Component {

    enum MoveDirection {
        UP, RIGHT, DOWN, LEFT
    }

    private CellMoveComponent moveComponent;

    private AStarMoveComponent astar;

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
//            // TODO: astar cancel movement
//            entity.setX((astar.getGrid().getWidth() - 1) * BLOCK_SIZE);
//            return;
//
//        } else if (x == astar.getGrid().getWidth() - 1 && currentMoveDir == RIGHT) {
//            entity.setX(0);
//            return;
//        }

        if (astar.isMoving())
            return;

        switch (nextMoveDir) {
            case UP:
                if (astar.getGrid().getUp(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case RIGHT:
                if (astar.getGrid().getRight(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case DOWN:
                if (astar.getGrid().getDown(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
            case LEFT:
                if (astar.getGrid().getLeft(x, y).filter(c -> c.getState().isWalkable()).isPresent())
                    currentMoveDir = nextMoveDir;
                break;
        }

        switch (currentMoveDir) {
            case UP:
                astar.moveToUpCell();
                break;
            case RIGHT:
                astar.moveToRightCell();
                break;
            case DOWN:
                astar.moveToDownCell();
                break;
            case LEFT:
                astar.moveToLeftCell();
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

}
