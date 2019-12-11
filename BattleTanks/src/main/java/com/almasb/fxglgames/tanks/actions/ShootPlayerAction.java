package com.almasb.fxglgames.tanks.actions;

import com.almasb.fxgl.ai.goap.MoveGoapAction;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.tanks.BattleTanksType;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import static java.lang.Math.abs;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShootPlayerAction extends MoveGoapAction {

    private LazyValue<Entity> player = new LazyValue<>(
            () -> FXGL.getGameWorld().getSingleton(BattleTanksType.PLAYER)
    );

    public ShootPlayerAction() {
        addEffect("playerAlive", false);
    }

    @Override
    public float getCost() {
        return (float) entity.distance(player.get());
    }

    @Override
    public boolean isInRange() {
        // TODO: generalize magic numbers
        int x = (int)((entity.getX() + 30/2 ) / 30);
        int y = (int)((entity.getY() + 30/2 ) / 30);

        int px = (int)((player.get().getX() + 30/2 ) / 30);
        int py = (int)((player.get().getY() + 30/2 ) / 30);

        return x == px || y == py;
    }

    @Override
    public void move() {
        int x = (int)((entity.getX() + 30/2 ) / 30);
        int y = (int)((entity.getY() + 30/2 ) / 30);

        int px = (int)((player.get().getX() + 30/2 ) / 30);
        int py = (int)((player.get().getY() + 30/2 ) / 30);

        AStarMoveComponent astar = entity.getComponent(AStarMoveComponent.class);

        if (!astar.isFinishedMoving() && !moveTimer.elapsed(moveInterval))
            return;

        List<AStarCell> validCells = astar.getGrid()
                .getCells()
                .stream()
                .filter(c -> c.getState().isWalkable() && (c.getX() == px || c.getY() == py) && distance(c, px, py) < 10)
                .collect(Collectors.toList());

        validCells = validCells.stream()
                //.sorted(Comparator.comparingInt(c -> distance(c, x, y)))
                //.limit(5)
                .collect(Collectors.toList());

        FXGLMath.random(validCells)
                .ifPresent(cell -> {
                    astar.moveToCell(cell.getX(), cell.getY());
                });


        moveInterval = Duration.seconds(FXGLMath.random(0.3, 1.0));
        moveTimer.capture();
    }

    private int distance(AStarCell cell1, int x, int y) {
        return abs(cell1.getX() - x) + abs(cell1.getY() - y);
    }

    @Override
    public void perform() {
        shoot();
        //isCompleted = true;
    }

    private LocalTimer shootTimer = FXGL.newLocalTimer();
    private LocalTimer moveTimer = FXGL.newLocalTimer();
    private Duration moveInterval = Duration.seconds(FXGLMath.random(0.1, 1.0));

    public void shoot() {
        if (!shootTimer.elapsed(Duration.seconds(1))) {
            return;
        }

        spawn("Bullet", new SpawnData(getEntity().getCenter())
                .put("direction", player.get().getPosition().subtract(entity.getPosition()))
                .put("owner", entity)
        );

        shootTimer.capture();
    }
}
