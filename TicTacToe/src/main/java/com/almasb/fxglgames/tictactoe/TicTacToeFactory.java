package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.input.MouseEvent;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TicTacToeFactory implements EntityFactory {

    @Spawns("tile")
    public Entity newTile(SpawnData data) {
        var tile = entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(getAppWidth() / 3, getAppWidth() / 3)))
                .with(new TileViewComponent())
                .build();

        tile.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> FXGL.<TicTacToeApp>getAppCast().onUserMove(tile));

        return tile;
    }
}
