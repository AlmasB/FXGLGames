package com.almasb.gravity.old;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Region;

import org.jbox2d.dynamics.BodyType;

public class Platform extends GameObject {

    public Platform(float x, float y, float w, float h) {
        super(x, y, w, h, BodyType.STATIC, true);

        Region r = new Region();
        r.setPrefSize(w, h);
        r.setBackground(new Background(new BackgroundImage(Config.Images.PLATFORM, null, null, null, null)));

        getChildren().add(r);
    }

    @Override
    public void onDeath() {
        // TODO Auto-generated method stub

    }

    @Override
    public Type getType() {
        return Type.PLATFORM;
    }

    @Override
    public void onCollide(GameObject other) {

    }

    @Override
    public void onUpdate() {
        // TODO Auto-generated method stub

    }
}
