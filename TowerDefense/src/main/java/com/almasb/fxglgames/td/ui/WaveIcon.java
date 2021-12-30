package com.almasb.fxglgames.td.ui;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

    public WaveIcon() {
        textCurrentWave.setTranslateX(5);
        textCurrentWave.setTranslateY(40);

        textWaveIn.setTranslateX(5);
        textWaveIn.setTranslateY(40);

        textWaveIn.textProperty().bind(countdown.asString("Wave in: %d"));

        timerCondition.bind(isCountdownGreaterZero);

        textCurrentWave.visibleProperty().bind(isCountdownGreaterZero.not());
        textWaveIn.visibleProperty().bind(isCountdownGreaterZero);

        getChildren().addAll(textCurrentWave, textWaveIn);
    }

    public void setMaxWave(int maxWave) {
        textCurrentWave.textProperty().bind(getip("currentWave").asString("Wave %d / " + maxWave));
    }

    public void scheduleWave(int seconds, Runnable action) {
        countdown.set(seconds);

        // TODO: change API to boolean binding / expression?
        getGameTimer().runAtIntervalWhile(() -> {

            countdown.set(countdown.get() - 1);

            if (countdown.get() == 0) {
                action.run();
            }

        }, Duration.seconds(1), timerCondition);
    }
}
