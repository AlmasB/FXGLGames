package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class CircularMoveComponent extends Component {

    private Point2D center;
    private double angle;

    public CircularMoveComponent(Point2D center, double angle) {
        this.center = center;
        this.angle = angle;
    }

    @Override
    public void onUpdate(double tpf) {
        angle += tpf * 60;

        var vector = Vec2.fromAngle(angle).mulLocal(150);
        entity.setPosition(vector.add(center));
    }
}
