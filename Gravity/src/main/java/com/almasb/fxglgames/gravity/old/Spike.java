package com.almasb.fxglgames.gravity.old;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Region;

import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

public class Spike extends GameObject {

    public Spike(float x, float y, float w, float h) {
        super(x, y, w, h, BodyType.STATIC, true);

        Region r = new Region();
        r.setPrefSize(w, h);
        r.setBackground(new Background(new BackgroundImage(Config.Images.SPIKE, null, null, null, null)));

        getChildren().add(r);
    }


    @Override
    public void onDeath() {
        // TODO Auto-generated method stub

    }

    @Override
    public Type getType() {
        return Type.SPIKE;
    }

    @Override
    public void onCollide(GameObject other) {
        other.onDeath();
    }


    @Override
    protected void onUpdate() {
        // TODO Auto-generated method stub

    }
}
