package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.ncc.Config.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(CardComponent.class)
public class CardViewComponent extends ChildViewComponent {

    private static final Map<Element, Color> outerBorderColors = Map.of(
            Element.NEUTRAL, Color.DARKGRAY.darker().darker(),
            Element.FIRE, Color.RED.brighter(),
            Element.WATER, Color.BLUE,
            Element.EARTH, Color.BROWN.darker(),
            Element.AIR, Color.LIGHTGRAY.darker()
    );

    private static final Map<Element, Color> innerBorderColors = Map.of(
            Element.NEUTRAL, Color.color(0.75, 0.75, 0.75, 0.8),
            Element.FIRE, Color.color(1, 0.8, 0.1, 0.8),
            Element.WATER, Color.color(0.0, 0.9, 0.9, 0.8),
            Element.EARTH, Color.color(0.6, 0.4, 0.3, 0.8).brighter(),
            Element.AIR, Color.color(0.8, 0.8, 0.8, 0.8).brighter()
    );

    private CardComponent card;

    @Override
    public void onAdded() {
        super.onAdded();

        Rectangle border = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        border.setStrokeWidth(2);
        border.setArcWidth(10);
        border.setArcHeight(10);

        Shape borderShape = Shape.union(border, new Circle(CARD_WIDTH / 2.0, 0.0, 30));
        borderShape.setFill(outerBorderColors.get(card.getElement()));
        borderShape.setStroke(Color.BLACK);

        var innerBorder = new Rectangle(CARD_WIDTH - 12, CARD_HEIGHT - 12);
        innerBorder.setFill(innerBorderColors.get(card.getElement()));
        innerBorder.setStroke(Color.BLACK);
        innerBorder.setStrokeWidth(2);
        innerBorder.setArcWidth(15);
        innerBorder.setArcHeight(15);

        innerBorder.setTranslateX(6);
        innerBorder.setTranslateY(6);



        var imageRect = texture("cards/" + card.getData().getImageName());
        imageRect.setTranslateX((CARD_WIDTH - CARD_IMAGE_WIDTH) / 2.0);
        imageRect.setTranslateY(45);

        var imageBorderRect = new Rectangle(CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, null);
        imageBorderRect.setStroke(Color.BLACK);
        imageBorderRect.setTranslateX(imageRect.getTranslateX());
        imageBorderRect.setTranslateY(imageRect.getTranslateY());


        var title = new Title(card.getName(), card.getLevel(), outerBorderColors.get(card.getElement()));
        title.setTranslateX(imageRect.getTranslateX());
        title.setTranslateY(7.5);

        var iconAtk = texture("icon_atk.png", 512 / 16.0, 512 / 16.0).brighter().saturate();

        Text textAtk = getUIFactory().newText("", Color.BLACK, 18);
        textAtk.textProperty().bind(card.atkProperty().asString("%d"));

        var boxAtk = new HBox(0, iconAtk, textAtk);
        boxAtk.setAlignment(Pos.CENTER_LEFT);
        boxAtk.setTranslateX(17.5);
        boxAtk.setTranslateY(CARD_HEIGHT - 70);

        var iconDef = texture("icon_def.png", 512 / 16.0, 512 / 16.0);

        Text textDef = getUIFactory().newText("", Color.BLACK, 18);
        textDef.textProperty().bind(card.defProperty().asString("%d"));

        var boxDef = new HBox(0, iconDef, textDef);
        boxDef.setAlignment(Pos.CENTER_LEFT);
        boxDef.setTranslateX(17.5);
        boxDef.setTranslateY(CARD_HEIGHT - 40);


        ProgressBar barHP = new ProgressBar(false);
        barHP.setMaxValue(card.getHp());
        barHP.currentValueProperty().bind(card.hpProperty());
        barHP.setFill(Color.LIGHTGREEN);
        barHP.setWidth(CARD_WIDTH / 3.2);
        barHP.setHeight(12.5);

        Text textHP = getUIFactory().newText("", Color.BLACK, 18);
        textHP.textProperty().bind(card.hpProperty().asString("%d"));

        var boxHP = new HBox(-10, barHP, textHP);
        boxHP.setAlignment(Pos.CENTER_LEFT);
        boxHP.setTranslateX(CARD_WIDTH / 3.0);
        boxHP.setTranslateY(CARD_HEIGHT - 65);

        ProgressBar barSP = new ProgressBar(false);
        barSP.setMaxValue(card.getSp());
        barSP.currentValueProperty().bind(card.spProperty());
        barSP.setFill(Color.BLUE);
        barSP.setWidth(CARD_WIDTH / 3.2);
        barSP.setHeight(12.5);

        Text textSP = getUIFactory().newText("", Color.BLACK, 18);
        textSP.textProperty().bind(card.spProperty().asString("%d"));

        var boxSP = new HBox(-10, barSP, textSP);
        boxSP.setAlignment(Pos.CENTER_LEFT);
        boxSP.setTranslateX(CARD_WIDTH / 3.0);
        boxSP.setTranslateY(CARD_HEIGHT - 40);

        Text textRarity = getUIFactory().newText(card.getRarity().toString().substring(0, 1), Color.color(0, 0, 0, 0.75), 34);
        centerText(textRarity, CARD_IMAGE_WIDTH / 2.0 + 20, 20);
        textRarity.setStroke(Color.WHITE);
        textRarity.setStrokeWidth(1.5);

        getViewRoot().getChildren().addAll(borderShape, innerBorder, imageRect, imageBorderRect, textRarity, title, boxAtk, boxDef, boxHP, boxSP);

        getViewRoot().setEffect(new DropShadow(10, -3.5, 10, Color.BLACK));

        getViewRoot().opacityProperty().bind(
                Bindings.when(card.aliveProperty()).then(1.0).otherwise(0.25)
        );
    }

//    public void setActive(boolean active) {
//        border.setStroke(active ? Color.GOLD : Color.BLUE);
//    }

    private static class Title extends HBox {

        Title(String name, int level, Color outlineColor) {
            super(-15);

            Circle circle = new Circle(20, 20, 20, Color.WHITE);
            circle.setStrokeWidth(2.0);
            circle.setStroke(outlineColor);

            var stack = new StackPane(circle, getUIFactory().newText("" + level, Color.BLACK, 30.0));

            Rectangle rect = new Rectangle(180, 30, Color.color(1, 1, 1, 0.8));
            rect.setStroke(Color.BLACK);

            getChildren().addAll(stack, new StackPane(rect, getUIFactory().newText(name, Color.BLACK, 16.0)));

            stack.toFront();
        }
    }
}
