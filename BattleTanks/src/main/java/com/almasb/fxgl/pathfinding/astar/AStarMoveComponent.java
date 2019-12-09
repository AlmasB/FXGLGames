package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.Grid;
import com.almasb.fxgl.pathfinding.CellMoveComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(CellMoveComponent.class)
public class AStarMoveComponent extends Component {

    private CellMoveComponent moveComponent;

    private AStarPathfinder pathfinder;

    private List<AStarCell> path = new ArrayList<>();

    public AStarMoveComponent(AStarPathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    public void moveToCell(int x, int y) {
        // TODO: block size via ctor
        int startX = (int)((entity.getX() + 30/2 ) / 30);
        int startY = (int)((entity.getY() + 30/2 ) / 30);

        path = pathfinder.findPath(startX, startY, x, y);
    }

    public boolean isMoving() {
        return moveComponent.isMoving();
    }

    public Grid<AStarCell> getGrid() {
        return pathfinder.getGrid();
    }

    @Override
    public void onUpdate(double tpf) {
        if (path.isEmpty() || moveComponent.isMoving())
            return;

        var next = path.remove(0);

        moveComponent.moveToCell(next.getX(), next.getY());
    }

    //    public void astar() {
//        if (position.getValue().equals(point))
//            return;
//
//        AStarGrid grid = ((PacmanApp) FXGL.getApp()).getGrid();
//

//
//        int targetX = (int)((point.getX() + 20) / PacmanApp.BLOCK_SIZE);
//        int targetY = (int)((point.getY() + 20) / PacmanApp.BLOCK_SIZE);
//
//        path = grid.getPath(startX, startY, targetX, targetY);
//    }
}
