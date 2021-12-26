package com.almasb.fxglgames.td.ui;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Icon extends Parent {

    public Icon() {
        var bg = new Rectangle(145, 64, Color.color(0.5, 0.5, 0.5, 0.75));
        bg.setStroke(Color.color(0, 0, 0, 0.9));
        bg.setStrokeWidth(2.0);
        bg.setArcWidth(10);
        bg.setArcHeight(10);

        getChildren().addAll(bg);
    }
}
