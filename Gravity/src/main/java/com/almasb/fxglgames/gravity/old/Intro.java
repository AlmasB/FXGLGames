package com.almasb.fxglgames.gravity.old;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Intro extends Parent {

    private SequentialTransition timeline;

    private int i = 0, j = 0;

    public Intro() {
        Region bg = new Region();
        bg.setPrefSize(Config.APP_W, Config.APP_H);
        bg.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Text textPresents = new Text("presents");
        textPresents.setFont(Font.font("Courier", FontPosture.ITALIC, 72));
        textPresents.setFill(Color.WHITE);
        textPresents.setTranslateX(Config.APP_W / 2 - 150);
        textPresents.setTranslateY(Config.APP_H / 2 + 70);
        textPresents.setOpacity(0);

        Text textLogo = new Text("GRAVITY");
        textLogo.setFont(Config.Fonts.LOGO);
        textLogo.setFill(Color.WHITE);
        textLogo.setTranslateX(Config.APP_W / 2 - 200);
        textLogo.setTranslateY(Config.APP_H / 2 + 200);
        textLogo.setOpacity(0);


        Word word = new Word("Alma", Font.font(null, FontPosture.ITALIC, 72));
        Word word2 = new Word("ABS", Font.font(72));
        Word word3 = new Word("oft", Font.font(null, FontPosture.ITALIC, 72));

        word.getChildren().forEach(node -> node.setOpacity(0));
        word3.getChildren().forEach(node -> node.setOpacity(0));

        HBox hbox = new HBox(word, word2, word3);
        hbox.setTranslateX(Config.APP_W / 2 - 200);
        hbox.setTranslateY(Config.APP_H / 2 - 150);

        getChildren().addAll(bg, hbox, textPresents, textLogo);

        // 'presents' animation
        FadeTransition fade = new FadeTransition(Duration.seconds(2), textPresents);
        fade.setToValue(1.0);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        // logo animation
        FadeTransition fade2 = new FadeTransition(Duration.seconds(2), textLogo);
        fade2.setToValue(1);

        ScaleTransition st = new ScaleTransition(Duration.seconds(1), textLogo);
        st.setToX(1.3);
        st.setToY(1.3);

        ParallelTransition pt = new ParallelTransition(fade2, st);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), textLogo);
        tt.setToY(150);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(2), hbox);
        fade3.setToValue(0);

        ParallelTransition pt2 = new ParallelTransition(tt, fade3);

        timeline = new SequentialTransition(fade, pt, pt2);

        // wait first
        KeyFrame kf = new KeyFrame(Duration.seconds(1), event -> {
            word.getChildren().forEach(node -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(5 - i), node);
                i++;
                ft.setToValue(1);
                ft.play();
            });
            word3.getChildren().forEach(node -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(2 + j), node);
                j++;
                ft.setToValue(1);
                ft.play();
            });

            Text t = ((Text)word2.getChildren().get(0));
            t.setFont(Font.font(null, FontPosture.ITALIC, 72));
            t.setText("s");
            t.setOpacity(0);

            FadeTransition ft = new FadeTransition(Duration.seconds(0.4), t);
            ft.setToValue(1);
            ft.play();
        });

        KeyFrame kf2 = new KeyFrame(Duration.seconds(4));

        Timeline t = new Timeline(kf, kf2);
        t.setOnFinished(event -> {
            timeline.play();
        });

        t.play();
    }

    private static class Word extends HBox {
        public Word(String word, Font f) {
            for (char c : word.toCharArray()) {
                Text letter = new Text(String.valueOf(c));
                letter.setFont(f);
                letter.setFill(Color.WHITE);

                getChildren().add(letter);
            }
        }
    }

    public void setOnFinished(EventHandler<ActionEvent> handler) {
        timeline.setOnFinished(handler);
    }
}
