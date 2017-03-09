package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.app.FXGL;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PauseWindow extends Pane {

    private int width;
    private int height;

    private int dx = 20;
    private int dy = 15;

    private boolean isOpen = false;

    private Runnable onPause;
    private Runnable onResume;

    public PauseWindow(int width, int height, Runnable onPause, Runnable onResume) {
        this.width = width;
        this.height = height;

        this.onPause = onPause;
        this.onResume = onResume;

        createBackground();
        createButton();
    }

    private void createBackground() {
        Polygon outer = new Polygon(
                0, dx,
                dx, 0,
                width * 2 / 3, 0,
                width * 2 / 3, 2*dy,
                width * 2 / 3 + 3*dx, 2*dy,
                width * 2 / 3 + 3*dx, 0,
                width - dx, 0,
                width, dx,
                width, height - dy,
                width - dy, height,
                dy, height,
                0, height - dy,
                0, height / 3 + 4*dy,
                dx, height / 3 + 5*dy,
                dx, height / 3 + 3*dy,
                0, height / 3 + 2*dy,
                0, height / 3,
                dx, height / 3 + dy,
                dx, height / 3 - dy,
                0, height / 3 - 2*dy
        );

        outer.setFill(Color.color(0.5, 0.5, 0.5, 0.75));
        outer.setStroke(Color.RED);
        outer.setStrokeWidth(6);
        outer.setStrokeType(StrokeType.CENTERED);

        getChildren().addAll(outer);
    }

    private void createButton() {
        Button btn = new Button("-");
        btn.setFont(FXGL.getUIFactory().newFont(16));
        btn.setPrefSize(3*dx, 2*dy);
        btn.setTranslateX(width * 2 / 3);
        btn.setOnAction(e -> onClick());

        getChildren().add(btn);
    }

    private void onClick() {
        if (isOpen) {
            close();
            isOpen = false;
            onResume.run();
        } else {
            open();
            isOpen = true;
            onPause.run();
        }
    }

    private void open() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.33), this);
        tt.setToY(50);
        tt.play();
    }

    private void close() {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.33), this);
        tt.setToY(FXGL.getApp().getHeight() - 30);
        tt.play();
    }
}
