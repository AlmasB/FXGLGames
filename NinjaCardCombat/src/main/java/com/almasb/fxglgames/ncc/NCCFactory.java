package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NCCFactory implements EntityFactory {

    @Spawns("card")
    public Entity newCard(SpawnData data) {
        Enum<?> type = data.get("type");

        return entityBuilder()
                .from(data)
                .type(type)
                .with(new CardComponent())
                .with(new CardViewComponent())
                .build();
    }
}
