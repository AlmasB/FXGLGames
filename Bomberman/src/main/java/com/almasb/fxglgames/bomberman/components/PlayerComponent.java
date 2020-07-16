package com.almasb.fxglgames.bomberman.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.bomberman.BombermanApp;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxgl.dsl.FXGL.spawn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private CellMoveComponent cell;
    private AStarMoveComponent astar;

    private int maxBombs = 1;
    private int bombsPlaced = 0;

    public void increaseMaxBombs() {
        maxBombs++;
    }

    public void placeBomb() {
        if (bombsPlaced == maxBombs) {
            return;
        }

        bombsPlaced++;

        Entity bomb = spawn("Bomb", new SpawnData(cell.getCellX() * 40, cell.getCellY() * 40).put("radius", BombermanApp.TILE_SIZE / 2));

        getGameTimer().runOnceAfter(() -> {
            bomb.getComponent(BombComponent.class).explode();
            bombsPlaced--;
        }, Duration.seconds(2));
    }

    public void moveRight() {
        astar.moveToRightCell();
    }

    public void moveLeft() {
        astar.moveToLeftCell();
    }

    public void moveUp() {
        astar.moveToUpCell();
    }

    public void moveDown() {
        astar.moveToDownCell();
    }
}
