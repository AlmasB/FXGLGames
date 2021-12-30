package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.core.util.EmptyRunnable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WaveIcon extends Icon {

    private Text textCurrentWave = getUIFactoryService().newText("", Color.WHITE, 24.0);
    private Text textWaveIn = getUIFactoryService().newText("", Color.WHITE, 24.0);

    private IntegerProperty countdown = new SimpleIntegerProperty(0);
    private BooleanBinding isCountdownGreaterZero = countdown.greaterThan(0);
    private BooleanProperty timerCondition = new SimpleBooleanProperty();

    private Button startButton = new Button("Start");

    private Runnable waveStartAction = EmptyRunnable.INSTANCE;

    public WaveIcon() {
        startButton.getStyleClass().add("wave_button");

        startButton.setOnAction(e -> {
            countdown.set(0);
            waveStartAction.run();
        });

        startButton.setTranslateX(150);
        startButton.setTranslateY(15);

        textCurrentWave.setTranslateX(5);
        textCurrentWave.setTranslateY(40);

        textWaveIn.setTranslateX(5);
        textWaveIn.setTranslateY(40);

        textWaveIn.textProperty().bind(countdown.asString("Wave in: %d"));

        timerCondition.bind(isCountdownGreaterZero);

        textCurrentWave.visibleProperty().bind(isCountdownGreaterZero.not());
        textWaveIn.visibleProperty().bind(isCountdownGreaterZero);
        startButton.visibleProperty().bind(isCountdownGreaterZero);

        getChildren().addAll(textCurrentWave, textWaveIn, startButton);
    }

    public void setMaxWave(int maxWave) {
        textCurrentWave.textProperty().bind(getip("currentWave").asString("Wave %d / " + maxWave));
    }

    public void scheduleWave(int seconds, Runnable action) {
        this.waveStartAction = action;
        countdown.set(seconds);

        // TODO: change API to boolean binding / expression?
        getGameTimer().runAtIntervalWhile(() -> {

            // just in case
            if (countdown.get() > 0) {
                countdown.set(countdown.get() - 1);

                if (countdown.get() == 0) {
                    action.run();
                }
            }
        }, Duration.seconds(1), timerCondition);
    }
}
