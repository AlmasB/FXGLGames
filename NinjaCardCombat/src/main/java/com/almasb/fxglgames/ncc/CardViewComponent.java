package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.getUIFactory;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(CardComponent.class)
public class CardViewComponent extends ChildViewComponent {

    private CardComponent card;

    private Rectangle border;

    @Override
    public void onAdded() {
        super.onAdded();

        border = new Rectangle(150, 220);
        border.setStroke(Color.BLUE);
        border.setStrokeWidth(2);

        border.fillProperty().bind(
                Bindings.when(card.aliveProperty()).then(Color.AQUA).otherwise(Color.RED)
        );

        Text textLevel = getUIFactory().newText("", Color.DARKGREY, 18.0);
        textLevel.textProperty().bind(card.levelProperty().asString("Lvl: %d"));
        textLevel.setTranslateX(5);
        textLevel.setTranslateY(50);

        Text textAtk = getUIFactory().newText("", Color.BLACK, 24);
        textAtk.textProperty().bind(card.atkProperty().asString("ATK: %d"));
        textAtk.setTranslateX(5);
        textAtk.setTranslateY(100);

        Text textDef = getUIFactory().newText("", Color.BLACK, 24);
        textDef.textProperty().bind(card.defProperty().asString("DEF: %d"));
        textDef.setTranslateX(5);
        textDef.setTranslateY(130);

        ProgressBar barHP = new ProgressBar(false);
        barHP.setMaxValue(card.getHp());
        barHP.currentValueProperty().bind(card.hpProperty());
        barHP.setFill(Color.LIGHTGREEN);
        barHP.setWidth(90);
        barHP.setHeight(10);
        barHP.setTranslateX(5);
        barHP.setTranslateY(150);

        ProgressBar barSP = new ProgressBar(false);
        barSP.setMaxValue(card.getSp());
        barSP.currentValueProperty().bind(card.spProperty());
        barSP.setFill(Color.BLUE);
        barSP.setWidth(90);
        barSP.setHeight(10);
        barSP.setTranslateX(5);
        barSP.setTranslateY(165);

        getViewRoot().getChildren().addAll(border, textLevel, textAtk, textDef, barHP, barSP);
    }

    public void setActive(boolean active) {
        border.setStroke(active ? Color.GOLD : Color.BLUE);
    }
}
