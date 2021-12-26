package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WaveIcon extends Icon {

    private Text text = FXGL.getUIFactoryService().newText("", Color.WHITE, 24.0);

    public WaveIcon() {
        text.setTranslateX(5);
        text.setTranslateY(40);

        getChildren().add(text);
    }

    public void setMaxWave(int maxWave) {
        text.textProperty().bind(FXGL.getip("currentWave").asString("Wave %d / " + maxWave));
    }
}
