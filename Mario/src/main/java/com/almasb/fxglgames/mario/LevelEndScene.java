package com.almasb.fxglgames.mario;

import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
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

    private Text textUserTime = getUIFactory().newText("", Color.WHITE, 24.0);
    private HBox gradeBox = new HBox();

    public LevelEndScene() {

        var bg = new Rectangle(WIDTH, HEIGHT, Color.color(0, 0, 0, 0.85));
        bg.setStroke(Color.BLUE);
        bg.setStrokeWidth(1.75);
        bg.setEffect(new DropShadow());

        VBox.setVgrow(gradeBox, Priority.ALWAYS);

        var vbox = new VBox(15, textUserTime, gradeBox, getUIFactory().newText("Tap to continue", Color.WHITE, 11.0));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(25));

        var root = new StackPane(
                bg, vbox
        );

        root.setTranslateX(1280 / 2 - WIDTH / 2);
        root.setTranslateY(720 / 2 - HEIGHT / 2);

        getChildren().addAll(root);

        getInput().addAction(new UserAction("Cancel") {
            @Override
            protected void onActionBegin() {
                getGameController().popSubScene();
            }
        }, MouseButton.PRIMARY);
    }

    public void onLevelFinish() {
        Duration userTime = Duration.seconds(getd("levelTime"));

        LevelTimeData timeData = geto("levelTimeData");

        textUserTime.setText(String.format("Your time: %.2f sec!", userTime.toSeconds()));

        gradeBox.getChildren().setAll(
                new Grade(Duration.seconds(timeData.star1), userTime),
                new Grade(Duration.seconds(timeData.star2), userTime),
                new Grade(Duration.seconds(timeData.star3), userTime)
        );

        getGameController().pushSubScene(this);
    }

    private static class Grade extends VBox {

        private static final Texture STAR_EMPTY = texture("star_empty.png", 65, 72);
        private static final Texture STAR_FULL = texture("star_full.png", 65, 72);

        public Grade(Duration gradeTime, Duration userTime) {
            super(15);

            HBox.setHgrow(this, Priority.ALWAYS);

            setAlignment(Pos.CENTER);

            getChildren().add(userTime.lessThanOrEqualTo(gradeTime) ? STAR_FULL.copy() : STAR_EMPTY.copy());

            getChildren().add(getUIFactory().newText(String.format("<%.2f", gradeTime.toSeconds()), Color.WHITE, 16.0));
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
