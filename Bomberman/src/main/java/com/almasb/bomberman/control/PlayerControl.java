package com.almasb.bomberman.control;

import com.almasb.bomberman.BombermanApp;
import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private PositionComponent position;
    private int maxBombs = 1;
    private int bombsPlaced = 0;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void increaseMaxBombs() {
        maxBombs++;
    }

    public void placeBomb() {
        if (bombsPlaced == maxBombs) {
            return;
        }

        bombsPlaced++;

        int x = position.getGridX(BombermanApp.TILE_SIZE);
        int y = position.getGridY(BombermanApp.TILE_SIZE);

        GameEntity bomb = FXGL.<BombermanApp>getAppCast()
                .getEntityFactory()
                .newBomb(x, y, BombermanApp.TILE_SIZE / 2);

        getEntity().getWorld().addEntity(bomb);

        FXGL.getMasterTimer().runOnceAfter(() -> {
            bomb.getControlUnsafe(BombControl.class).explode();
            bombsPlaced--;
        }, Duration.seconds(2));
    }

    public void moveRight() {
        position.translateX(BombermanApp.TILE_SIZE);
    }

    public void moveLeft() {
        position.translateX(-BombermanApp.TILE_SIZE);
    }

    public void moveUp() {
        position.translateY(-BombermanApp.TILE_SIZE);
    }

    public void moveDown() {
        position.translateY(BombermanApp.TILE_SIZE);
    }
}
