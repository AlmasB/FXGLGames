package com.almasb.fxglgames.rts;

import com.almasb.fxgl.dsl.EntityBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class RTSFactory implements EntityFactory {

    @Spawns("unit")
    public Entity newUnit(SpawnData data) {
        return entityBuilder()
                .from(data)
                .view(new Rectangle(40, 40))
                .build();
    }
}
