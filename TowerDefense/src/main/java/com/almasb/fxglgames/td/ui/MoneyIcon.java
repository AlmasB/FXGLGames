package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.animation.Interpolators;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoneyIcon extends Icon {

    public MoneyIcon() {
        var moneyTexture = texture("money.png").multiplyColor(Color.GOLD);

        var text = getUIFactoryService().newText("", Color.WHITE, 32.0);
        text.textProperty().bind(getip("money").asString());
        text.setTranslateX(54);
        text.setTranslateY(45);

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            var t = moneyTexture.copy();

            animationBuilder()
                    .onFinished(() -> getChildren().remove(t))
                    .duration(Duration.seconds(0.5))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .translate(t)
                    .from(new Point2D(0, -64))
                    .to(new Point2D(0, 0))
                    .buildAndPlay();

            getChildren().add(t);
        });

        getChildren().addAll(moneyTexture, text);
    }
}
