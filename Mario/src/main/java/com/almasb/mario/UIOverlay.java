/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.mario;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.service.AssetLoader;
import com.almasb.fxgl.texture.Texture;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UIOverlay extends Parent {

    private static final Font FONT = Font.font(18);

    private double width, height;

    private Text textDebug = new Text();
    private Text textScore = new Text();
    private Text textLives = new Text();
    private Text textGhostBomb = new Text();
    private Text textManual = new Text();

    private Texture textureLives;
    private Texture textureGhostBomb;

    private ImageView selectedItemView = new ImageView();

    public UIOverlay(double w, double h) {
        this.width = w;
        this.height = h;

        AssetLoader assetLoader = FXGL.getAssetLoader();

        textDebug.setTranslateY(50);
        textDebug.setFont(FONT);

        textScore.setTranslateX(1100);
        textScore.setTranslateY(50);
        textScore.setFont(FONT);

        textureLives = assetLoader.loadTexture("life.png");
        textureLives.setTranslateX(1100);
        textureLives.setTranslateY(75);

        textLives.setTranslateX(1130);
        textLives.setTranslateY(90);
        textLives.setFont(FONT);

        textureGhostBomb = assetLoader.loadTexture("ghost_bomb.png");
        textureGhostBomb.setTranslateX(1100);
        textureGhostBomb.setTranslateY(100);

        textGhostBomb.setTranslateX(1130);
        textGhostBomb.setTranslateY(120);
        textGhostBomb.setFont(FONT);

        textManual.setTranslateY(200);
        textManual.setFont(FONT);
        textManual.setText("W - Jump\n"
                + "A/D - Move\n"
                + "F - Spawn Bomb\n"
                + "RightClick - Select Target for Bomb\n");

        getChildren().addAll(textDebug, textScore, textLives, textGhostBomb, textManual,
                textureLives, textureGhostBomb,
                createSelectedItemView());
    }

    private Node createSelectedItemView() {
        double radius = 25;

        Circle circle = new Circle(radius);
        circle.setFill(null);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(3);
//        circle.setTranslateX(radius + 3);
//        circle.setTranslateY(height - radius - 3);



        StackPane stack = new StackPane(circle, selectedItemView);
        stack.setAlignment(Pos.CENTER);
        stack.setTranslateY(height - 2*radius - 3);

        return stack;
    }

    public void bindSelectedItemView(ObjectProperty<Image> image) {
        selectedItemView.imageProperty().bind(image);
    }

    public void bindTextScore(IntegerProperty score) {
        textScore.textProperty().bind(new SimpleStringProperty("Score: ").concat(score));
    }

    public void bindTextLives(IntegerProperty lives) {
        textLives.textProperty().bind(new SimpleStringProperty("x").concat(lives));
    }

    public void bindTextGhostBombs(IntegerProperty bombs) {
        textGhostBomb.textProperty().bind(new SimpleStringProperty("x").concat(bombs));
    }

    public void setDebugMessage(String text) {
        textDebug.setText(text);
    }

    public void playLifePulseAnimation(Runnable action) {
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.3), textureLives);
        st.setToX(1.5);
        st.setToY(1.5);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.setOnFinished(e -> {
            action.run();
        });
        st.play();
    }

    public void playLifeDropAnimation(Runnable action) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), textureLives);
        tt.setToY(height);
        tt.setOnFinished(e -> {
            action.run();
            textureLives.setTranslateY(75);
        });
        tt.play();
    }

    public void playScoreAnimation(int value, Point2D position, Runnable action) {
        Text t = new Text(String.valueOf(value));
        t.setTranslateX(position.getX());
        t.setTranslateY(position.getY());

        getChildren().add(t);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), t);
        tt.setToX(1140);
        tt.setToY(50);
        tt.setOnFinished(event -> {
            action.run();
            getChildren().remove(t);
        });
        tt.play();
    }

    public void showMessage(String message) {
        HBox letters = new HBox(0);
        double w = 0;
        for (int i = 0; i < message.length(); i++) {
            Text letter = new Text(message.charAt(i) + "");
            letter.setFont(Font.font(48));
            w += letter.getLayoutBounds().getWidth();

            TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), letter);
            tt.setDelay(Duration.seconds(i * 0.1));
            tt.setToY(height / 2);
            tt.play();

            letters.getChildren().add(letter);

            if (i == message.length() - 1) {
                tt.setOnFinished(e -> {
                    FadeTransition ft = new FadeTransition(Duration.seconds(2), letters);
                    ft.setToValue(0);
                    ft.setOnFinished(e2 -> getChildren().remove(letters));
                    ft.play();
                });
            }
        }

        letters.setTranslateX(width / 2 - w / 2);
        getChildren().add(letters);
    }

    public void showMessageFlash(String message) {
        Text text = new Text(message);
        text.setTranslateX(width / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setTranslateY(height / 2);
        text.setFont(FONT);
        text.setOpacity(0);

        getChildren().add(text);

        FadeTransition ft = new FadeTransition(Duration.seconds(0.33), text);
        ft.setToValue(1);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.setOnFinished(e -> getChildren().remove(text));
        ft.play();
    }
}
