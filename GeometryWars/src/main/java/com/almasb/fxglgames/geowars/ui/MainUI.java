package com.almasb.fxglgames.geowars.ui;

import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class MainUI extends Parent {

    public MainUI() {
        Text multText = getUIFactoryService().newText("", Color.WHITE, 12);
        multText.setTranslateX(2.5);
        multText.setTranslateY(20);
        multText.textProperty().bind(getip("multiplier").asString("x%d"));

        multText.textProperty().addListener((obs, old, newValue) -> {
            animationBuilder()
                    .interpolator(Interpolators.ELASTIC.EASE_IN())
                    .repeat(2)
                    .autoReverse(true)
                    .duration(Duration.seconds(0.4))
                    .scale(multText)
                    .from(new Point2D(1.0, 1.0))
                    .to(new Point2D(1.8, 1.8))
                    .buildAndPlay();
        });

        Text scoreText = getUIFactoryService().newText("", Color.WHITE, 28);
        scoreText.textProperty().bind(getip("score").asString());
        scoreText.setStroke(Color.GOLD);

        var livesBar = new HBox(3.5);
        livesBar.setTranslateY(multText.getTranslateY() + 12);

        for (int i = 0; i < geti("lives"); i++) {
            var t = texture("PlayerNew.png", 16, 16);

            livesBar.getChildren().add(t);
        }

        getWorldProperties().<Integer>addListener("lives", (prev, now) -> {
            if (now < prev && !livesBar.getChildren().isEmpty()) {
                livesBar.getChildren().remove(livesBar.getChildren().size() - 1);
            }
        });

        var centerLine = new Line(0, 0, 0, 150);
        centerLine.setStroke(Color.RED);

        getChildren().addAll(scoreText, multText, livesBar);
    }
}
