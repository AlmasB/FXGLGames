package com.almasb.fxglgames.td.ui;

import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoneyIcon extends Icon {

    public MoneyIcon() {
        var moneyTexture = texture("money.png");

        var text = getUIFactoryService().newText("", Color.WHITE, 32.0);
        text.textProperty().bind(getip("money").asString());
        text.setTranslateX(54);
        text.setTranslateY(45);

        getChildren().addAll(moneyTexture, text);
    }
}
