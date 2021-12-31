package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxglgames.td.CurrencyService;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CurrencyView extends Parent {

    public CurrencyView() {
        var bg = new Rectangle(100, 34, Color.color(0.5, 0.5, 0.5, 0.75));
        bg.setStroke(Color.color(0, 0, 0, 0.9));
        bg.setStrokeWidth(2.5);
        bg.setArcWidth(10);
        bg.setArcHeight(10);

        var icon = new CurrencyIcon();
        icon.setTranslateX(15);
        icon.setTranslateY(bg.getHeight() / 2.0);

        var text = FXGL.getUIFactoryService().newText("", Color.WHITE, 22.0);
        text.setTranslateX(55);
        text.setTranslateY(25);

        text.textProperty().bind(FXGL.getService(CurrencyService.class).currencyProperty().asString());

        getChildren().addAll(bg, icon, text);
    }
}
