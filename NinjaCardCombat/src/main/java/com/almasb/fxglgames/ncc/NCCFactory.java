package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

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
        var card = FXGLMath.random(DECKS)
                .map(Deck::getCards)
                .flatMap(FXGLMath::random)
                .get();

        return entityBuilder()
                .from(data)
                .with(new CardComponent(card))
                .with(new CardViewComponent())
                .build();
    }
}
