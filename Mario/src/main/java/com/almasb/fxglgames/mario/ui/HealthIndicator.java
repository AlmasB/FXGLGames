package com.almasb.fxglgames.mario.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxglgames.mario.components.HPComponent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HealthIndicator extends StackPane {

    private static final int RADIUS = 45;

    private HPComponent playerHP;

    private Circle inner;

    public HealthIndicator(HPComponent playerHP) {
        this.playerHP = playerHP;

        var outer = new Circle(RADIUS, RADIUS, RADIUS, null);
        outer.setStroke(Color.GREEN);
        outer.setStrokeWidth(6.0);

        outer.strokeProperty().bind(
                Bindings.when(playerHP.valueProperty().divide(playerHP.getMaxHP() * 1.0).greaterThan(0.25)).then(Color.GREEN).otherwise(Color.RED)
        );

        inner = new Circle(RADIUS - 2, RADIUS - 2, RADIUS - 2, Color.LIGHTGREEN.brighter());

        inner.fillProperty().bind(
                Bindings.when(playerHP.valueProperty().divide(playerHP.getMaxHP() * 1.0).greaterThan(0.25)).then(Color.LIGHTGREEN.brighter()).otherwise(Color.RED.brighter())
        );

        playerHP.valueProperty().addListener((o, old, hp) -> {
            hpChanged(hp.intValue());
        });

        getChildren().addAll(inner, outer);
    }

    private void hpChanged(int hp) {
        var timeline = new Timeline();
        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(0.66), new KeyValue(inner.radiusProperty(), hp * 1.0 / playerHP.getMaxHP() * RADIUS, Interpolators.BOUNCE.EASE_OUT()))
        );
        timeline.play();
    }
}
