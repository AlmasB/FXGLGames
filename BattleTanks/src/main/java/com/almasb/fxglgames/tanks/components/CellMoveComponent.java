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

    private Point2D point = new Point2D(0, 0);

    private int cellWidth;
    private int cellHeight;

    private boolean isMoving = false;

    public CellMoveComponent(int cellWidth, int cellHeight) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void moveTo(int cellX, int cellY) {
        point = new Point2D(cellX, cellY);
        isMoving = true;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isMoving)
            return;

        double speed = tpf * 60 * 5;

        var next = point;

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
            isMoving = false;
        }
    }
}
