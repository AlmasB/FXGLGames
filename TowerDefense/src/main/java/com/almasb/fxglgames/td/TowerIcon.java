package com.almasb.fxglgames.td;

import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerIcon extends StackPane {

    // TODO: cost, tooltip
    public TowerIcon(TowerData data) {
        var bg = new Circle(35, 35, 35);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2.5);

        getChildren().addAll(bg, texture(data.getImageName()));
    }
}
