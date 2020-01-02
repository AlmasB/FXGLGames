package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getUIFactory;
import static com.almasb.fxglgames.ncc.Config.CARD_HEIGHT;
import static com.almasb.fxglgames.ncc.Config.CARD_WIDTH;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NCCFactory implements EntityFactory {

    public static final List<Deck> DECKS;

    static {
        DECKS = CardKt.loadDecks();
    }

    @Spawns("card")
    public Entity newCard(SpawnData data) {
        Card card = data.get("card");

        return entityBuilder()
                .from(data)
                .with(new CardComponent(card))
                .with(new CardViewComponent())
                .build();
    }

    @Spawns("cardPlaceholder")
    public Entity newPlaceholder(SpawnData data) {
        var bg = new Region();
        bg.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        bg.setStyle(
                "-fx-background-color: darkgray;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 5;" +
                "-fx-border-style: segments(10, 15, 15, 15)  line-cap round;"
        );

        var text = getUIFactory().newText(data.hasKey("isPlayer") ? "+" : "?", Color.WHITE, 76);
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);

        return entityBuilder()
                .from(data)
                .view(new StackPane(bg, text))
                .build();
    }
}
