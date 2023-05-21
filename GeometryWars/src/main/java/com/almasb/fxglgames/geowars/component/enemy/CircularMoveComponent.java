package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class CircularMoveComponent extends Component {

    private Point2D center;
    private double angle;
    private double distance;
    private boolean cw;

    public CircularMoveComponent(Point2D center, double angle, double distance, boolean cw) {
        this.center = center;
        this.angle = angle;
        this.distance = distance;
        this.cw = cw;
    }

    public void setCenter(Point2D center) {
        this.center = center;
    }

    @Override
    public void onUpdate(double tpf) {
        angle += cw ? tpf * 60 : - tpf * 60;

        var vector = Vec2.fromAngle(angle).mulLocal(distance);
        entity.setPosition(vector.add(center));
    }
}
