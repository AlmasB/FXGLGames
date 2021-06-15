package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxglgames.tanks.BattleTanksType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class TankAIComponent extends Component {

    /**
     * Global AI blackboard to keep track of where each AI is moving to.
     */
    private static final List<AStarCell> MOVE_TO_CELLS = new ArrayList<>();

    private StateComponent state;
    private LazyValue<AStarMoveComponent> astar = new LazyValue<>(() -> {
        return entity.getComponent(AStarMoveComponent.class);
    });

    private TankViewComponent tank;

    // TODO: currently this is only for the 5 enemies of the player, need to generalize
    private Entity player;
    private Entity ownFlag;
    private Entity enemyFlag;

    private AStarCell moveToCell = null;

    private EntityState GUARD_OWN_FLAG = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState entityState) {
            clearMoveCell();

            var flagCell = getCell(ownFlag);

            astar.get()
                    .getGrid()
                    .getWalkableCells()
                    .stream()
                    .filter(cell -> !MOVE_TO_CELLS.contains(cell))
                    .min(Comparator.comparingInt(cell -> cell.distance(flagCell)))
                    .ifPresent(cell -> moveTo(cell));
        }

        @Override
        protected void onUpdate(double tpf) {
            if (astar.get().isAtDestination()) {
                Entity closestEnemy;

                if (entity.isType(ENEMY)) {
                    closestEnemy = getGameWorld()
                            .getClosestEntity(entity, e -> e.isType(ALLY) || e.isType(PLAYER))
                            .get();
                } else {
                    closestEnemy = getGameWorld()
                            .getClosestEntity(entity, e -> e.isType(ENEMY))
                            .get();
                }

                shoot(closestEnemy);
            }
        }
    };

    private EntityState ATTACK_ENEMY_FLAG = new EntityState() {

        @Override
        public void onEnteredFrom(EntityState entityState) {
            clearMoveCell();

            var flagCell = getCell(enemyFlag);

            astar.get()
                    .getGrid()
                    .getWalkableCells()
                    .stream()
                    .filter(cell -> !MOVE_TO_CELLS.contains(cell))
                    .filter(cell -> cell.getX() == flagCell.getX() || cell.getY() == flagCell.getY())
                    .min(Comparator.comparingInt(cell -> cell.distance(flagCell)))
                    .ifPresent(cell -> moveTo(cell));
        }

        @Override
        protected void onUpdate(double tpf) {
            if (astar.get().isAtDestination()) {
                shoot(enemyFlag);
            }
        }
    };

    private void shoot(Entity target) {
        var tankCell = getCell(entity);
        var flagCell = getCell(target);

        if (tankCell.getX() == flagCell.getX()) {
            if (tankCell.getY() < flagCell.getY()) {
                entity.setRotation(90);
            } else {
                entity.setRotation(270);
            }
        } else {
            if (tankCell.getX() < flagCell.getX()) {
                entity.setRotation(0);
            } else {
                entity.setRotation(180);
            }
        }

        tank.shoot();
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) {
            player = getGameWorld().getSingleton(PLAYER);

            if (entity.isType(ALLY)) {
                ownFlag = getGameWorld().getSingleton(PLAYER_FLAG);
                enemyFlag = getGameWorld().getSingleton(ENEMY_FLAG);
            } else {
                ownFlag = getGameWorld().getSingleton(ENEMY_FLAG);
                enemyFlag = getGameWorld().getSingleton(PLAYER_FLAG);
            }
        }

        if (entity.isType(ENEMY)) {
            var closestEnemyToOwnFlag = getGameWorld()
                    .getClosestEntity(ownFlag, e -> e.isType(ALLY) || e.isType(PLAYER))
                    .get();

            if (closestEnemyToOwnFlag.distanceBBox(ownFlag) < 200) {
                state.changeState(GUARD_OWN_FLAG);
            } else {
                state.changeState(ATTACK_ENEMY_FLAG);
            }

        } else {
            var closestEnemyToOwnFlag = getGameWorld()
                    .getClosestEntity(ownFlag, e -> e.isType(ENEMY))
                    .get();

            if (closestEnemyToOwnFlag.distanceBBox(ownFlag) < 200) {
                state.changeState(GUARD_OWN_FLAG);
            } else {
                state.changeState(ATTACK_ENEMY_FLAG);
            }
        }

        if (astar.get().isAtDestination()) {
            clearMoveCell();
        }
    }

    private void clearMoveCell() {
        if (moveToCell != null) {
            MOVE_TO_CELLS.remove(moveToCell);
            moveToCell = null;
        }
    }

    private void moveTo(AStarCell cell) {
        moveToCell = cell;
        MOVE_TO_CELLS.add(cell);

        astar.get().moveToCell(cell);
    }

    private AStarCell getCell(Entity entity) {

        int x = (int) ((entity.getX() + 30 / 2) / 30);
        int y = (int) ((entity.getY() + 30 / 2) / 30);

        return astar.get().getGrid().get(x, y);
    }
}
