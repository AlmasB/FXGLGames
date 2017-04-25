package com.almasb.fxglgames.gravity.old;

public class Portal extends GameObject {

    public Portal(float x, float y) {
        super(x, y, Config.BLOCK_SIZE, Config.BLOCK_SIZE, null, false);
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

    }

    @Override
    public Type getType() {
        return Type.PORTAL;
    }

}
