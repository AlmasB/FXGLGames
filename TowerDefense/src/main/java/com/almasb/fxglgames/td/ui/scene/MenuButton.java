package com.almasb.fxglgames.td.ui.scene;

import com.almasb.fxgl.ui.FontType;
import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

public class MenuButton extends Parent {

    public MenuButton(String name, Runnable action) {
        var bg = new Polygon(
                0.0, 0.0,
                200.0, 0.0,
                200.0, 25.0,
                190.0, 40.0,
                0.0, 40.0
        );
        bg.setStrokeWidth(2.5);
        bg.strokeProperty().bind(
                Bindings.when(hoverProperty()).then(Color.GOLD).otherwise(Color.TRANSPARENT)
        );
        bg.fillProperty().bind(
                Bindings.when(hoverProperty()).then(Color.DARKORANGE).otherwise(Color.TRANSPARENT)
        );

        var text = getUIFactoryService().newText(name, Color.WHITE, FontType.GAME, 26.0);
        text.setTranslateX(15);
        text.setTranslateY(28);
        text.fillProperty().bind(
                Bindings.when(disableProperty())
                        .then(Color.GRAY)
                        .otherwise(
                                Bindings.when(pressedProperty()).then(Color.YELLOWGREEN).otherwise(Color.WHITE)
                        )
        );

        setOnMouseClicked(e -> action.run());

        getChildren().addAll(bg, text);
    }
}