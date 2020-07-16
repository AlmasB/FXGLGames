package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TutorialSubScene extends SubScene {

    private boolean isAnimating = false;

    public TutorialSubScene() {
        var textTutorial = getUIFactoryService().newText("Tap the circle to change ball color\n\n" +
                "Tap the left side of the screen to move left!\n\n" +
                "Tap the right side of the screen to move right!", Color.WHITE, FontType.TEXT, 20);
        textTutorial.setWrappingWidth(250);
        textTutorial.setTextAlignment(TextAlignment.LEFT);

        var bg = new Rectangle(300, 230, Color.color(0.3627451f, 0.3627451f, 0.5627451f, 0.55));
        bg.setArcWidth(50);
        bg.setArcHeight(50);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(10);

        var stackPane = new StackPane(bg, textTutorial);

        getContentRoot().setTranslateX(-350);
        getContentRoot().setTranslateY(250);
        getContentRoot().getChildren().add(stackPane);

        getContentRoot().setOnMouseClicked(e -> {
            if (isAnimating)
                return;

            isAnimating = true;

            animationBuilder()
                    .onFinished(() -> getSceneService().popSubScene())
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .translate(getContentRoot())
                    .from(new Point2D(50, 250))
                    .to(new Point2D(-350, 250))
                    .buildAndPlay(TutorialSubScene.this);
        });
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(getContentRoot())
                .from(new Point2D(-350, 250))
                .to(new Point2D(50, 250))
                .buildAndPlay(this);
    }
}
