package com.almasb.fxglgames.td.ui;

import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SellIcon extends Icon {

    public SellIcon() {
        var moneyTexture = texture("money.png").multiplyColor(Color.GOLD);

        var text = getUIFactoryService().newText("Sell", Color.WHITE, 32.0);
        text.setTranslateX(54);
        text.setTranslateY(45);

        getChildren().addAll(moneyTexture, text);
    }
}
