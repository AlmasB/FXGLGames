package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class AutoRotationComponent extends Component {

    private Point2D prev;
    private Entity e;

    public AutoRotationComponent() {
        this.e = entity;
    }

    public void setEntity(Entity e){
        this.e = e;
    }

    public Point2D getPrev() {
        return prev;
    }

    @Override
    public void onAdded() {
        prev = this.e.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        var vector = this.e.getPosition().subtract(prev);

        var now = this.e.getRotation();
        var next = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));

        this.e.setRotation(now * 0.9 + next * 0.1);

        prev = this.e.getPosition();
    }
}