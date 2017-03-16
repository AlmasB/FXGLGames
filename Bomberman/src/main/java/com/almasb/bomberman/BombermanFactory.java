package com.almasb.bomberman;

import com.almasb.bomberman.control.BombControl;
import com.almasb.bomberman.control.PlayerControl;
import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class BombermanFactory implements TextEntityFactory {

    @Spawns("BG")
    public GameEntity newBackground(SpawnData data) {
        return Entities.builder()
                .at(0, 0)
                .viewFromNode(new EntityView(new Rectangle(600, 600, Color.LIGHTGREEN), RenderLayer.BACKGROUND))
                .build();
    }

    @SpawnSymbol('w')
    public GameEntity newWall(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.WALL)
                .from(data)
                .viewFromNode(new Rectangle(40, 40, Color.GRAY.saturate()))
                .build();
    }

    @SpawnSymbol('b')
    public GameEntity newBrick(SpawnData data) {
        return Entities.builder()
                .type(BombermanType.BRICK)
                .from(data)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("brick.png", 40, 40))
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
                .viewFromNodeWithBBox(new Circle(BombermanApp.TILE_SIZE / 2, Color.BLACK))
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
