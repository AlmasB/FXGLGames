package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TutorialSubScene extends SubScene {

    public TutorialSubScene() {
        var textTutorial = getUIFactoryService().newText("Press SPACE to change ball color\n\n" +
                "The ball only interacts with bricks of the same color!", Color.WHITE, FontType.TEXT, 20);
        textTutorial.setWrappingWidth(250);
        textTutorial.setTextAlignment(TextAlignment.LEFT);

        var bg = new Rectangle(300, 170, Color.color(0.3627451f, 0.3627451f, 0.5627451f, 0.55));
        bg.setArcWidth(50);
        bg.setArcHeight(50);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(10);

        var stackPane = new StackPane(bg, textTutorial);

        getContentRoot().setTranslateX(-250);
        getContentRoot().setTranslateY(250);
        getContentRoot().getChildren().add(stackPane);

        getInput().addAction(new UserAction("Space") {
            @Override
            protected void onActionBegin() {
                animationBuilder()
                        .onFinished(() -> getSceneService().popSubScene())
                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                        .translate(getContentRoot())
                        .from(new Point2D(50, 250))
                        .to(new Point2D(-250, 250))
                        .buildAndPlay(TutorialSubScene.this);
            }
        }, KeyCode.SPACE);
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(getContentRoot())
                .from(new Point2D(-250, 250))
                .to(new Point2D(50, 250))
                .buildAndPlay(this);
    }
}
