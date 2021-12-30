package com.almasb.fxglgames.td.ui;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PowerDownIcon extends Polygon {

    public PowerDownIcon(Color fill) {
        getPoints().addAll(
                0.0, 0.0, 32.0, 32.0, 64.0, 0.0,
                60.0, -5.0, 32.0, 25.0, 4.0, -5.0
        );

        setStrokeWidth(2.0);
        setStroke(Color.ORANGERED);
        setFill(fill);
    }
}
