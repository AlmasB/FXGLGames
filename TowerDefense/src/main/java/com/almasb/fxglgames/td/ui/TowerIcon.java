package com.almasb.fxglgames.td.ui;

import com.almasb.fxglgames.td.TowerData;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerIcon extends VBox {

    public TowerIcon(TowerData data) {
        var bg = new Circle(35, 35, 35);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2.5);

        var text = getUIFactoryService().newText(data.cost() + "");
        text.setStroke(Color.BLACK);

        var texture = texture(data.imageName());

        var stackPane = new StackPane(bg, texture);

        setSpacing(10);

        getChildren().addAll(stackPane, text);

        texture.opacityProperty().bind(
                Bindings.when(getip("money").greaterThanOrEqualTo(data.cost()))
                        .then(1.0)
                        .otherwise(0.25)
        );
    }
}
