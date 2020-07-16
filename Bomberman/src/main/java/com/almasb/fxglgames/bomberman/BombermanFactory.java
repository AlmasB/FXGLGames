package com.almasb.fxglgames.bomberman;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.bomberman.components.BombComponent;
import com.almasb.fxglgames.bomberman.components.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.bomberman.BombermanApp.TILE_SIZE;
import static com.almasb.fxglgames.bomberman.BombermanType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombermanFactory implements EntityFactory {

    @Spawns("BG")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .at(0, 0)
                .view(new Rectangle(600, 600, Color.LIGHTGREEN))
                .zIndex(-1)
                .build();
    }

    @Spawns("w")
    public Entity newWall(SpawnData data) {
        return entityBuilder(data)
                .type(WALL)
                .viewWithBBox(new Rectangle(40, 40, Color.GRAY.saturate()))
                .build();
    }

    @Spawns("b")
    public Entity newBrick(SpawnData data) {
        return entityBuilder(data)
                .type(BRICK)
                .viewWithBBox(texture("brick.png", 40, 40))
                .build();
    }

    @Spawns("Player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data)
                .atAnchored(new Point2D(20, 20), new Point2D(20, 20))
                .type(PLAYER)
                .viewWithBBox(new Rectangle(TILE_SIZE, TILE_SIZE, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new CellMoveComponent(40, 40, 80))
                .with(new AStarMoveComponent(FXGL.<BombermanApp>getAppCast().getGrid()))
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("Bomb")
    public Entity newBomb(SpawnData data) {
        return entityBuilder(data)
                .type(BOMB)
                .viewWithBBox("bomb.png")
                .with(new BombComponent(data.get("radius")))
                .atAnchored(new Point2D(13, 11), new Point2D(data.getX() + TILE_SIZE / 2, data.getY() + TILE_SIZE / 2))
                .build();
    }

    @Spawns("Powerup")
    public Entity newPowerup(SpawnData data) {
        return entityBuilder(data)
                .type(POWERUP)
                .viewWithBBox(new Rectangle(TILE_SIZE, TILE_SIZE, Color.YELLOW))
                .with(new CollidableComponent(true))
                .build();
    }
}
