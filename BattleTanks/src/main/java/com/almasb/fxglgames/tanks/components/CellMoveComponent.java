package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: ctor take in speed
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CellMoveComponent extends Component {

    private List<Point2D> path = new ArrayList<>();

    private int cellWidth;
    private int cellHeight;

    public CellMoveComponent(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    // TODO:
    public void astar() {
        //position = getEntity().getPositionComponent();
//
//        if (position.getValue().equals(point))
//            return;
//
//        AStarGrid grid = ((PacmanApp) FXGL.getApp()).getGrid();
//
//        int startX = (int)(position.getX() / PacmanApp.BLOCK_SIZE);
//        int startY = (int)(position.getY() / PacmanApp.BLOCK_SIZE);
//
//        int targetX = (int)((point.getX() + 20) / PacmanApp.BLOCK_SIZE);
//        int targetY = (int)((point.getY() + 20) / PacmanApp.BLOCK_SIZE);
//
//        path = grid.getPath(startX, startY, targetX, targetY);
//
//        // we use A*, so no need for that
//        getEntity().getComponentOptional(MoveControl.class).ifPresent(c -> c.pause());
    }

    public void moveTo(int cellX, int cellY) {
        path.add(new Point2D(cellX, cellY));
    }

    @Override
    public void onUpdate(double tpf) {
        if (path.isEmpty())
            return;

        double speed = tpf * 60 * 5;

        var next = path.get(0);

        int nextX = (int) next.getX() * cellWidth;
        int nextY = (int) next.getY() * cellHeight;

        // 5 is offset, TODO: remove
        double dx = nextX - entity.getX() + 5;
        double dy = nextY - entity.getY() + 5;

        if (Math.abs(dx) <= speed)
            entity.setX(nextX + 5);
        else
            entity.translateX(speed * Math.signum(dx));

        if (Math.abs(dy) <= speed)
            entity.setY(nextY + 5);
        else
            entity.translateY(speed * Math.signum(dy));

        if ((int) entity.getX() == nextX + 5 && (int) entity.getY() == nextY + 5) {
            path.remove(0);
        }
    }
}
