package com.almasb.fxglgames.mario.view;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TextViewComponent extends ChildViewComponent {

    public TextViewComponent(String message) {
        super(0, 35);

        getViewRoot().getChildren().add(FXGL.getUIFactory().newText(message, Color.WHITE, 24));
    }
}
