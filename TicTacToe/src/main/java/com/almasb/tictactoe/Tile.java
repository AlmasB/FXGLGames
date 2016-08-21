package com.almasb.tictactoe;

import com.almasb.fxgl.app.FXGL;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Tile extends StackPane {

    private TicTacToeApp app;
    private TileValue value = TileValue.NONE;

    private Arc arc = new Arc(34, 37, 34, 37, 0, 0);
    private Line line1 = new Line(0, 0, 0, 0);
    private Line line2 = new Line(75, 0, 75, 0);

    public Tile() {
        app = FXGL.getAppCast();

        Rectangle bg = new Rectangle(app.getWidth() / 3, app.getHeight() / 3, Color.rgb(13, 222, 236));

        Rectangle bg2 = new Rectangle(app.getWidth() / 4, app.getHeight() / 4, Color.rgb(250, 250, 250, 0.25));
        bg2.setArcWidth(25);
        bg2.setArcHeight(25);

        arc.setFill(null);
        arc.setStroke(Color.BLACK);
        arc.setStrokeWidth(3);

        line1.setStrokeWidth(3);
        line2.setStrokeWidth(3);

        line1.setVisible(false);
        line2.setVisible(false);

        getChildren().addAll(bg, bg2, arc, line1, line2);

        setOnMouseClicked(e -> app.onUserMove(this));
    }

    public TileValue getValue() {
        return value;
    }

    public double getCenterX() {
        return getTranslateX() + app.getWidth() / 3 / 2;
    }

    public double getCenterY() {
        return getTranslateY() + app.getHeight() / 3 / 2;
    }

    public boolean mark(TileValue value) {
        if (this.value != TileValue.NONE)
            return false;

        this.value = value;

        if (value == TileValue.O) {
            KeyFrame frame = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(arc.lengthProperty(), 360));

            Timeline timeline = new Timeline(frame);
            timeline.play();
        } else {

            line1.setVisible(true);
            line2.setVisible(true);

            KeyFrame frame1 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line1.endXProperty(), 75),
                    new KeyValue(line1.endYProperty(), 75));

            KeyFrame frame2 = new KeyFrame(Duration.seconds(0.5),
                    new KeyValue(line2.endXProperty(), 0),
                    new KeyValue(line2.endYProperty(), 75));

            Timeline timeline = new Timeline(frame1, frame2);
            timeline.play();
        }

        return true;
    }
}
