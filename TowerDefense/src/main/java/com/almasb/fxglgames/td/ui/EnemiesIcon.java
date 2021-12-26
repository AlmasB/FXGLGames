package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemiesIcon extends Icon {

    private Text text = FXGL.getUIFactoryService().newText("", Color.WHITE, 24.0);

    public EnemiesIcon() {
        text.setTranslateX(5);
        text.setTranslateY(40);

        text.textProperty().bind(FXGL.getip("numEnemies").asString("Left: %d"));

        getChildren().add(text);
    }
}
