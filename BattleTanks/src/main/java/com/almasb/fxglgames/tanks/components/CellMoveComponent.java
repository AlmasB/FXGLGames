package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

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

        // TODO: remove
        var offset = -3;

        double dx = nextX + offset - entity.getX();
        double dy = nextY + offset - entity.getY();

        if ((dx) > 0) {
            entity.setRotation(0);
        } else if ((dx) < 0) {
            entity.setRotation(180);
        } else if ((dy) > 0) {
            entity.setRotation(90);
        } else if ((dy) < 0) {
            entity.setRotation(270);
        }

        if (abs(dx) <= speed)
            entity.setX(nextX + offset);
        else
            entity.translateX(speed * Math.signum(dx));

        if (abs(dy) <= speed)
            entity.setY(nextY + offset);
        else
            entity.translateY(speed * Math.signum(dy));

        if ((int) entity.getX() == nextX + offset && (int) entity.getY() == nextY + offset) {
            isMoving = false;

            //System.out.println(entity.getTransformComponent());
        }
    }
}
