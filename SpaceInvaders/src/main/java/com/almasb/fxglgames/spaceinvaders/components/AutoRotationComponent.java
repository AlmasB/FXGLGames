package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class AutoRotationComponent extends Component {

    private Point2D prev;

    @Override
    public void onAdded() {
        prev = entity.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        var vector = entity.getPosition().subtract(prev);

        var now = entity.getRotation();
        var next = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

        entity.setRotation(now * 0.9 + next * 0.1);

        prev = entity.getPosition();
    }
}