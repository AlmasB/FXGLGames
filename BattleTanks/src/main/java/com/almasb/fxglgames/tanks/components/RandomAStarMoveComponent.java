package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.Grid;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RandomAStarMoveComponent extends Component {

    private AStarMoveComponent astar;

    private LocalTimer moveTimer;
    private Duration moveInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));

    @Override
    public void onAdded() {
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (moveTimer.elapsed(moveInterval)) {
            if (!astar.isMoving()) {
                astar.getGrid()
                        .getRandomCell(c -> c.getState().isWalkable())
                        .ifPresent(cell -> {
                            astar.moveToCell(cell.getX(), cell.getY());
                        });
            }

            moveInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));
            moveTimer.capture();
        }
    }
}
