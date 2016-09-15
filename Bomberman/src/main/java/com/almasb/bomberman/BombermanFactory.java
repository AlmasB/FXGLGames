package com.almasb.bomberman;

import com.almasb.bomberman.control.BombControl;
import com.almasb.bomberman.control.PlayerControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.parser.EntityFactory;
import com.almasb.fxgl.parser.EntityProducer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombermanFactory extends EntityFactory {

    public BombermanFactory() {
        super('0');
    }

    @EntityProducer('w')
    public GameEntity newWall(int x, int y) {
        return Entities.builder()
                .type(EntityType.WALL)
                .at(x * BombermanApp.TILE_SIZE, y * BombermanApp.TILE_SIZE)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE))
                .build();
    }

    public GameEntity newPlayer(int x, int y) {
        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(x * BombermanApp.TILE_SIZE, y * BombermanApp.TILE_SIZE)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    public GameEntity newBomb(int x, int y, double radius) {
        return Entities.builder()
                .type(EntityType.BOMB)
                .at(x * BombermanApp.TILE_SIZE, y * BombermanApp.TILE_SIZE)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.RED))
                .with(new BombControl(radius))
                .build();
    }

    public GameEntity newPowerup(int x, int y) {
        return Entities.builder()
                .type(EntityType.POWERUP)
                .at(x * BombermanApp.TILE_SIZE, y * BombermanApp.TILE_SIZE)
                .viewFromNodeWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }
}
