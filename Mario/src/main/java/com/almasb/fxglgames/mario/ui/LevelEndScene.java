package com.almasb.fxglgames.mario.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontFactory;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LevelEndScene extends SubScene {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 250;

    private Text textUserTime = getUIFactoryService().newText("", Color.WHITE, 24.0);
    private HBox gradeBox = new HBox();

    private FontFactory levelFont = getAssetLoader().loadFont("level_font.ttf");
    private BooleanProperty isAnimationDone = new SimpleBooleanProperty(false);

    public LevelEndScene() {
        var bg = new Rectangle(WIDTH, HEIGHT, Color.color(0, 0, 0, 0.85));
        bg.setStroke(Color.BLUE);
        bg.setStrokeWidth(1.75);
        bg.setEffect(new DropShadow(28, Color.color(0,0,0, 0.9)));

        VBox.setVgrow(gradeBox, Priority.ALWAYS);

        var textContinue = getUIFactoryService().newText("Tap to continue", Color.WHITE, 11.0);
        textContinue.visibleProperty().bind(isAnimationDone);

        animationBuilder(this)
                .repeatInfinitely()
                .autoReverse(true)
                .scale(textContinue)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.25, 1.25))
                .buildAndPlay();

        var vbox = new VBox(15, textUserTime, gradeBox, textContinue);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));

        var root = new StackPane(
                bg, vbox
        );

        root.setTranslateX(1280 / 2 - WIDTH / 2);
        root.setTranslateY(720 / 2 - HEIGHT / 2);

        var textLevel = new Text();
        textLevel.textProperty().bind(getip("level").asString("Level %d"));
        textLevel.setFont(levelFont.newFont(52));
        textLevel.setRotate(-20);

        textLevel.setFill(Color.ORANGE);
        textLevel.setStroke(Color.BLACK);
        textLevel.setStrokeWidth(3.5);

        textLevel.setTranslateX(root.getTranslateX() - textLevel.getLayoutBounds().getWidth() / 3);
        textLevel.setTranslateY(root.getTranslateY() + 25);

        getContentRoot().getChildren().addAll(root, textLevel);

        getInput().addAction(new UserAction("Close Level End Screen") {
            @Override
            protected void onActionBegin() {
                if (!isAnimationDone.getValue())
                    return;

                getSceneService().popSubScene();
            }
        }, MouseButton.PRIMARY);
    }

    public void onLevelFinish() {
        isAnimationDone.setValue(false);

        Duration userTime = Duration.seconds(getd("levelTime"));

        LevelTimeData timeData = geto("levelTimeData");

        textUserTime.setText(String.format("Your time: %.2f sec!", userTime.toSeconds()));

        gradeBox.getChildren().setAll(
                new Grade(Duration.seconds(timeData.star1), userTime),
                new Grade(Duration.seconds(timeData.star2), userTime),
                new Grade(Duration.seconds(timeData.star3), userTime)
        );

        for (int i = 0; i < gradeBox.getChildren().size(); i++) {
            var builder = animationBuilder(this).delay(Duration.seconds(i * 0.75))
                    .duration(Duration.seconds(0.75))
                    .interpolator(Interpolators.ELASTIC.EASE_OUT());

            // if last star animation
            if (i == gradeBox.getChildren().size() - 1) {
                builder = builder.onFinished(() -> isAnimationDone.setValue(true));
            }

            builder.translate(gradeBox.getChildren().get(i))
                    .from(new Point2D(0, -500))
                    .to(new Point2D(0, 0))
                    .buildAndPlay();
        }

        getSceneService().pushSubScene(this);
    }

    private static class Grade extends VBox {

        private static final Texture STAR_EMPTY = texture("star_empty.png", 65, 72).darker();
        private static final Texture STAR_FULL = texture("star_full.png", 65, 72);

        public Grade(Duration gradeTime, Duration userTime) {
            super(15);

            HBox.setHgrow(this, Priority.ALWAYS);

            setAlignment(Pos.CENTER);

            getChildren().add(userTime.lessThanOrEqualTo(gradeTime) ? STAR_FULL.copy() : STAR_EMPTY.copy());

            getChildren().add(getUIFactoryService().newText(String.format("<%.2f", gradeTime.toSeconds()), Color.WHITE, 16.0));
        }
    }

    public static class LevelTimeData {

        private final double star1;
        private final double star2;
        private final double star3;

        /**
         * @param star1 in seconds
         * @param star2 in seconds
         * @param star3 in seconds
         */
        public LevelTimeData(double star1, double star2, double star3) {
            this.star1 = star1;
            this.star2 = star2;
            this.star3 = star3;
        }
    }
}
