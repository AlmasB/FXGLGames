package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.td.data.TowerData;
import com.almasb.fxglgames.td.data.Vars;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerIcon extends VBox {

    private Texture texture;
    private TowerData data;

    public TowerIcon(TowerData data) {
        this.data = data;

        var bg = new Circle(35, 35, 35);
        bg.setStroke(Color.WHITE);
        bg.setStrokeWidth(2.5);

        var text = getUIFactoryService().newText(data.cost() + "");
        text.setStroke(Color.BLACK);

        texture = texture(data.imageName());

        var stackPane = new StackPane(bg, texture);

        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(stackPane, text);
    }

    public void bindToMoney(IntegerProperty property) {
        texture.opacityProperty().bind(
                Bindings.when(property.greaterThanOrEqualTo(data.cost()))
                        .then(1.0)
                        .otherwise(0.25)
        );
    }
}
