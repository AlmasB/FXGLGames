package com.almasb.fxglgames.td.ui;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoneyIcon extends Parent {

    public MoneyIcon() {
        var bg = new Rectangle(145, 64, Color.color(0.5, 0.5, 0.5, 0.75));
        bg.setStroke(Color.color(0, 0, 0, 0.9));
        bg.setStrokeWidth(2.0);
        bg.setArcWidth(10);
        bg.setArcHeight(10);

        var moneyTexture = texture("money.png");

        var text = getUIFactoryService().newText("", Color.WHITE, 32.0);
        text.textProperty().bind(getip("money").asString());
        text.setTranslateX(54);
        text.setTranslateY(45);

        getChildren().addAll(bg, moneyTexture, text);
    }
}
