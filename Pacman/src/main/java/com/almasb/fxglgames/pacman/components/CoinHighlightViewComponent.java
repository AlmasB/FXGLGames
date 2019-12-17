package com.almasb.fxglgames.pacman.components;

import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CoinHighlightViewComponent extends ChildViewComponent {

    public CoinHighlightViewComponent() {
        super(5, 5);

        Rectangle rect = new Rectangle(30, 30, Color.color(0.8, 0, 0, 0.9));
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setEffect(new Bloom(0.9));

        getViewRoot().getChildren().add(rect);
    }
}
