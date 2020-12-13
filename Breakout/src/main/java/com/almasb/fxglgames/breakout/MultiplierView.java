package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.ui.FontType;
import javafx.geometry.Point2D;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MultiplierView extends VBox {

    private Text text = getUIFactoryService().newText("", Color.YELLOW, FontType.GAME, 200);

    private Text assessmentText = getUIFactoryService().newText("", Color.YELLOW, FontType.GAME, 30);

    public MultiplierView() {
        super(0);

        text.setStrokeWidth(2.5);
        text.setStroke(Color.BLACK);
        text.setRotate(-22);

        assessmentText.setTranslateX(10);
        assessmentText.setTranslateY(-20);

        getChildren().addAll(text, assessmentText);
    }

    public Text getText() {
        return text;
    }

    public Text getAssessmentText() {
        return assessmentText;
    }

    /**
     * Play multiplier change animation.
     */
    public void playAnimation() {
        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(0.06))
                .repeat(2)
                .autoReverse(true)
                .scale(this)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.25, 1.25))
                .buildAndPlay();
    }
}
