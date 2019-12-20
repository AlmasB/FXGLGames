package com.almasb.fxglgames.tanks.components.ai;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.AccumulatedUpdateComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxglgames.tanks.BattleTanksType.PLAYER;
import static com.almasb.fxglgames.tanks.BattleTanksType.PLAYER_FLAG;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public class GuardComponent extends AccumulatedUpdateComponent {

    private boolean isChasingPlayer;

    private AStarMoveComponent astar;
    private AStarGrid grid;

    private Entity flag;
    private AStarCell flagCell;

    private List<AStarCell> guardCells;

    public GuardComponent(AStarGrid grid, boolean chasePlayer) {
        super(90);
        this.grid = grid;
        isChasingPlayer = chasePlayer;
    }

    @Override
    public void onAdded() {
        flag = getGameWorld().getSingleton(PLAYER_FLAG);
        flagCell = getCellValue(flag);

        guardCells = grid.getCells()
                .stream()
                .filter(cell -> cell.getState() == CellState.WALKABLE)
                .sorted(Comparator.comparingInt(this::distanceToFlag))
                .limit(20)
                .collect(Collectors.toList());

//        guardCells.forEach(cell -> {
//
//            addUINode(new Rectangle(30, 30, Color.color(0, 0, 0.8, 0.65)), cell.getX() * 30, cell.getY() * 30);
//        });
    }

    private int distanceToFlag(AStarCell cell) {
        var d1 = distance(cell, flagCell);
        var d2 = distance(cell, grid.get(flagCell.getX() + 1, flagCell.getY() + 1));
        var d3 = distance(cell, grid.get(flagCell.getX() + 0, flagCell.getY() + 1));
        var d4 = distance(cell, grid.get(flagCell.getX() + 1, flagCell.getY() + 0));

        return min(min(d1, d2), min(d3, d4));
    }

    private int distance(AStarCell cell1, AStarCell cell2) {
        return abs(cell1.getX() - cell2.getX()) + abs(cell1.getY() - cell2.getY());
    }

    @Override
    public void onAccumulatedUpdate(double tpfSum) {
        if (isChasingPlayer) {
            flag = getGameWorld().getSingleton(PLAYER);
            flagCell = getCellValue(flag);

            guardCells = grid.getCells()
                    .stream()
                    .filter(cell -> cell.getState() == CellState.WALKABLE)
                    .sorted(Comparator.comparingInt(this::distanceToFlag))
                    .limit(20)
                    .collect(Collectors.toList());
        }

        if (astar.isMoving())
            return;

        AStarCell randomCell = FXGLMath.random(guardCells).get();

        astar.moveToCell(randomCell.getX(), randomCell.getY());
    }

    private AStarCell getCellValue(Entity entity) {
        // TODO: generalize magic numbers
        int x = (int) ((entity.getX() + 30 / 2) / 30);
        int y = (int) ((entity.getY() + 30 / 2) / 30);

        return grid.get(x, y);
    }
}
