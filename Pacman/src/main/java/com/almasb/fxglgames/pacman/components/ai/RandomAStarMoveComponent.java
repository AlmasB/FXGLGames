package com.almasb.fxglgames.pacman.components.ai;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import java.util.function.Supplier;

/**
 * TODO: add to FXGL
 * TODO: move to cell avoiding busy cells
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(AStarMoveComponent.class)
public class RandomAStarMoveComponent extends Component {

    private AStarMoveComponent astar;

    private LocalTimer moveTimer;
    private Duration moveInterval;
    private Supplier<Duration> moveIntervalSupplier;

    public RandomAStarMoveComponent() {
        this(Duration.seconds(0.2), Duration.seconds(1));
    }

    public RandomAStarMoveComponent(Duration minInterval, Duration maxInterval) {
        moveIntervalSupplier = () -> Duration.seconds(FXGLMath.random(minInterval.toSeconds(), maxInterval.toSeconds()));
        moveInterval = moveIntervalSupplier.get();
    }

    @Override
    public void onAdded() {
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (moveTimer.elapsed(moveInterval)) {
            if (astar.isAtDestination()) {
                astar.getGrid()
                        .getRandomCell(c -> c.getState().isWalkable())
                        .ifPresent(astar::moveToCell);
            }

            moveInterval = moveIntervalSupplier.get();
            moveTimer.capture();
        }
    }
}
