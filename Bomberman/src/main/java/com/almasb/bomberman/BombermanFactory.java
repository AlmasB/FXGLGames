package com.almasb.bomberman;

import com.almasb.bomberman.control.BombControl;
import com.almasb.bomberman.control.PlayerControl;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.TextEntityFactory;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class BombermanFactory implements TextEntityFactory {

    @SpawnSymbol('w')
    public GameEntity newWall(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.WALL)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(40, 40))
                .build();
    }

    @Spawns("Player")
    public GameEntity newPlayer(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.PLAYER)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    @Spawns("Bomb")
    public GameEntity newBomb(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.BOMB)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.RED))
                .with(new BombControl(data.get("radius")))
                .build();
    }

    @Spawns("Powerup")
    public GameEntity newPowerup(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.POWERUP)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }

    @Override
    public char emptyChar() {
        return '0';
    }

    @Override
    public int blockWidth() {
        return 40;
    }

    @Override
    public int blockHeight() {
        return 40;
    }
}
