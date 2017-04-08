package com.almasb.gravity.old;

public class Viewport extends GameObject {

    public Viewport() {
        super(0, 0, Config.appWidth.get(), Config.appHeight.get(), null, false);

        Config.resolutionScale.addListener((obs, old, newValue) -> {
            this.width = Config.appWidth.get();
            this.height = Config.appHeight.get();
        });
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
        // TODO Auto-generated method stub

    }

    @Override
    public Type getType() {
        // TODO Auto-generated method stub
        return null;
    }

}
