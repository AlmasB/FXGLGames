package com.almasb.fxglgames.bomberman;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.bomberman.components.BombComponent;
import com.almasb.fxglgames.bomberman.components.PlayerComponent;
import com.almasb.fxgl.entity.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombermanFactory implements EntityFactory {

    @Spawns("BG")
    public Entity newBackground(SpawnData data) {
        return FXGL.entityBuilder()
                .at(0, 0)
                .view(new Rectangle(600, 600, Color.LIGHTGREEN))
                .zIndex(-1)
                .build();
    }

    @Spawns("w")
    public Entity newWall(SpawnData data) {
        return FXGL.entityBuilder()
                .type(BombermanType.WALL)
                .from(data)
                .view(new Rectangle(40, 40, Color.GRAY.saturate()))
                .build();
    }

    @Spawns("b")
    public Entity newBrick(SpawnData data) {
        return FXGL.entityBuilder()
                .type(BombermanType.BRICK)
                .from(data)
                .viewWithBBox(FXGL.getAssetLoader().loadTexture("brick.png", 40, 40))
                .build();
    }

    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        return FXGL.entityBuilder()
                .type(BombermanType.PLAYER)
                .from(data)
                .viewWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("Bomb")
    public Entity newBomb(SpawnData data) {
        return FXGL.entityBuilder()
                .type(BombermanType.BOMB)
                .from(data)
                .viewWithBBox(new Circle(BombermanApp.TILE_SIZE / 2, BombermanApp.TILE_SIZE / 2, BombermanApp.TILE_SIZE / 2, Color.BLACK))
                .with(new BombComponent(data.get("radius")))
                .build();
    }

    @Spawns("Powerup")
    public Entity newPowerup(SpawnData data) {
        return FXGL.entityBuilder()
                .type(BombermanType.POWERUP)
                .from(data)
                .viewWithBBox(new Rectangle(BombermanApp.TILE_SIZE, BombermanApp.TILE_SIZE, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }
}
