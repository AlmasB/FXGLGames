package com.almasb.fxglgames.td.ui;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CurrencyIcon extends Parent {

    public CurrencyIcon() {
        var circleOuter = new Circle(20, Color.YELLOW);
        circleOuter.setStroke(Color.BLACK);
        circleOuter.setStrokeWidth(4.0);

        var circleInner = new Circle(11, Color.YELLOW.brighter());
        circleInner.setStroke(Color.BLACK);
        circleInner.setStrokeWidth(2.0);
        circleInner.setTranslateY(-4);

        getChildren().addAll(circleOuter, circleInner);
    }
}
