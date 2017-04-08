package com.almasb.gravity.old;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import org.jbox2d.dynamics.BodyType;

public class Coin extends GameObject {

    private ImageView sprite;

    private Timeline timeline;

    public Coin(float x, float y) {
        super(x, y, Config.BLOCK_SIZE, Config.BLOCK_SIZE, BodyType.KINEMATIC, false);

        sprite = new ImageView(Config.Images.COIN);
        sprite.setFitHeight(Config.BLOCK_SIZE);
        sprite.setFitWidth(Config.BLOCK_SIZE);
        sprite.setViewport(new Rectangle2D(0, 0, 128, 128));

        getChildren().add(sprite);

        SimpleIntegerProperty frameProperty = new SimpleIntegerProperty();
        frameProperty.addListener((obs, old, newValue) -> {
            if (newValue.intValue() > old.intValue())
                sprite.setViewport(new Rectangle2D((newValue.intValue() % 8) * 128, (newValue.intValue() / 8) * 128, 128, 128));
        });

        timeline = new Timeline(new KeyFrame(Duration.seconds(2), new KeyValue(frameProperty, 23)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public void onUpdate() {
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
            Config.Audio.COIN.play();
            onDeath();
        }
    }

    @Override
    public Type getType() {
        return Type.COIN;
    }
}
