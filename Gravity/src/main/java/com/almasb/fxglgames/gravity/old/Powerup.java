package com.almasb.fxglgames.gravity.old;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

public class Powerup extends GameObject {

    public enum PowerType {
        HP, GRAVITY
    }

    private PowerType type;

    private ImageView sprite;

    private Timeline timeline;

    public Powerup(float x, float y, PowerType type) {
        super(x, y, Config.BLOCK_SIZE, Config.BLOCK_SIZE, BodyType.KINEMATIC, false);

        this.type = type;

        sprite = new ImageView(Config.Images.POWERUP);
        sprite.setFitHeight(Config.BLOCK_SIZE);
        sprite.setFitWidth(Config.BLOCK_SIZE);
        sprite.setViewport(new Rectangle2D(0, 0, 81, 81));

        getChildren().add(sprite);

        SimpleIntegerProperty frameProperty = new SimpleIntegerProperty();
        frameProperty.addListener((obs, old, newValue) -> {
            if (newValue.intValue() > old.intValue())
                sprite.setViewport(new Rectangle2D(newValue.intValue() * 81, (type.ordinal() + 1) * 81, 81, 81));
        });

        timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(frameProperty, 7)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    protected void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeath() {
        dying = true;
        timeline.stop();
        alive = false;
    }

    @Override
    public void onCollide(GameObject other) {
        if (other.getType() == Type.PLAYER) {
            Config.Audio.POWERUP.play();
            onDeath();
        }
    }

    @Override
    public Type getType() {
        return Type.POWERUP;
    }

    public PowerType getPowerType() {
        return type;
    }
}
