package com.almasb.fxglgames.td.ui;

import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HPIcon extends Icon {

    public HPIcon() {
        var hpTexture = texture("health.png").multiplyColor(Color.RED);

        var text = getUIFactoryService().newText("", Color.WHITE, 32.0);
        text.textProperty().bind(getip("playerHP").asString());
        text.setTranslateX(54);
        text.setTranslateY(45);

        getChildren().addAll(hpTexture, text);
    }
}
