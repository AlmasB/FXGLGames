package com.almasb.gravity.old;

import org.jbox2d.dynamics.BodyType;

public class Obstacle extends GameObject {

    public Obstacle(float x, float y, float width, float height) {
        super(x, y, width, height, BodyType.STATIC, true);

    }

    @Override
    protected void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeath() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCollide(GameObject other) {
        if (other.getType() == Type.STONE) {

        }
    }

    @Override
    public Type getType() {
        return Type.OBSTACLE;
    }
}
