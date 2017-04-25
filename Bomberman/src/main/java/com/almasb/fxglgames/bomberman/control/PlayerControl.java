package com.almasb.fxglgames.bomberman.control;

import com.almasb.fxglgames.bomberman.BombermanApp;
import com.almasb.fxglgames.bomberman.BombermanType;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import javafx.geometry.Point2D;
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

        Entity bomb = FXGL.getApp()
                .getGameWorld()
                .spawn("Bomb", new SpawnData(x * 40, y * 40).put("radius", BombermanApp.TILE_SIZE / 2));

        FXGL.getMasterTimer().runOnceAfter(() -> {
            bomb.getControlUnsafe(BombControl.class).explode();
            bombsPlaced--;
        }, Duration.seconds(2));
    }

    public void moveRight() {
        if (canMove(new Point2D(40, 0)))
            position.translateX(BombermanApp.TILE_SIZE);
    }

    public void moveLeft() {
        if (canMove(new Point2D(-40, 0)))
            position.translateX(-BombermanApp.TILE_SIZE);
    }

    public void moveUp() {
        if (canMove(new Point2D(0, -40)))
            position.translateY(-BombermanApp.TILE_SIZE);
    }

    public void moveDown() {
        if (canMove(new Point2D(0, 40)))
            position.translateY(BombermanApp.TILE_SIZE);
    }

    private boolean canMove(Point2D direction) {
        Point2D newPosition = position.getValue().add(direction);

        return FXGL.getApp()
                .getGameScene()
                .getViewport()
                .getVisibleArea()
                .contains(newPosition)

                &&

                FXGL.getApp()
                .getGameWorld()
                .getEntitiesAt(newPosition)
                .stream()
                .filter(e -> e.hasComponent(TypeComponent.class))
                .map(e -> e.getComponentUnsafe(TypeComponent.class))
                .filter(type -> type.isType(BombermanType.BRICK)
                        || type.isType(BombermanType.WALL)
                        || type.isType(BombermanType.BOMB))
                .count() == 0;
    }
}
