package com.almasb.ncc;

import com.almasb.fxgl.entity.EntityView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CardFrame extends StackPane {

    private Rectangle border;

    public CardFrame() {
        border = new Rectangle(80, 80);
        border.setFill(Color.AQUA);
        border.setStroke(Color.BLUE);
        border.setStrokeWidth(2);

        getChildren().addAll(border);
    }

    public void addCardView(EntityView view) {
        getChildren().addAll(view);
    }

    public void setActive(boolean active) {
        border.setStroke(active ? Color.GOLD : Color.BLUE);
    }
}
