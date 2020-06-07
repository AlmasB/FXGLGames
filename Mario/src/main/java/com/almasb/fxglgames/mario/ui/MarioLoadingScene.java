package com.almasb.fxglgames.mario.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.LoadingScene;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioLoadingScene extends LoadingScene {

    public MarioLoadingScene() {
        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.AZURE);

        var text = getUIFactoryService().newText("Loading level", Color.BLACK, 46.0);
        centerText(text, getAppWidth() / 2, getAppHeight() / 3  + 25);

        var hbox = new HBox(5);

        for (int i = 0; i < 3; i++) {
            var textDot = getUIFactoryService().newText(".", Color.BLACK, 46.0);

            hbox.getChildren().add(textDot);

            animationBuilder(this)
                    .autoReverse(true)
                    .delay(Duration.seconds(i * 0.5))
                    .repeatInfinitely()
                    .fadeIn(textDot)
                    .buildAndPlay();
        }

        hbox.setTranslateX(getAppWidth() / 2 - 20);
        hbox.setTranslateY(getAppHeight() / 2);

        var playerTexture = texture("player.png").subTexture(new Rectangle2D(0, 0, 32, 42));
        playerTexture.setTranslateX(getAppWidth() / 2 - 32/2);
        playerTexture.setTranslateY(getAppHeight() / 2 - 42/2);

        animationBuilder(this)
                .duration(Duration.seconds(1.25))
                .repeatInfinitely()
                .autoReverse(true)
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .rotate(playerTexture)
                .from(0)
                .to(360)
                .buildAndPlay();

        getContentRoot().getChildren().setAll(bg, text, hbox, playerTexture);
    }
}
