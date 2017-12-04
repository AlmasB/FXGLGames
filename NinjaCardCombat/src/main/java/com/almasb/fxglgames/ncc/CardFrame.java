package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.entity.view.EntityView;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
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

    public CardFrame(Card card) {
        border = new Rectangle(120, 180);
        border.setStroke(Color.BLUE);
        border.setStrokeWidth(2);

        border.fillProperty().bind(
                Bindings.when(card.aliveProperty()).then(Color.AQUA).otherwise(Color.RED)
        );

        getChildren().addAll(border);
    }

    public void addCardView(EntityView view) {
        getChildren().addAll(view);
    }

    public void setActive(boolean active) {
        border.setStroke(active ? Color.GOLD : Color.BLUE);
    }
}
