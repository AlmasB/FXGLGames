package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.entity.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileCombo {

    private Entity tile1, tile2, tile3;
    private List<Entity> tiles;

    public TileCombo(Entity tile1, Entity tile2, Entity tile3) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;

        tiles = Arrays.asList(tile1, tile2, tile3);
    }

    public Entity getTile1() {
        return tile1;
    }

    public Entity getTile2() {
        return tile2;
    }

    public Entity getTile3() {
        return tile3;
    }

    public boolean isComplete() {
        return !isEmpty(tile1)
                && getValueOf(tile1) == getValueOf(tile2)
                && getValueOf(tile1) == getValueOf(tile3);
    }

    /**
     * @return true if all tiles are empty
     */
    public boolean isOpen() {
        return tiles.stream()
                .allMatch(this::isEmpty);
    }

    /**
     * @param value tile value
     * @return true if this combo has 2 of value and an empty slot
     */
    public boolean isTwoThirds(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> getValueOf(t) == oppositeValue))
            return false;

        return tiles.stream()
                .filter(this::isEmpty)
                .count() == 1;
    }

    /**
     * @param value tile value
     * @return true if this combo has 1 of value and 2 empty slots
     */
    public boolean isOneThird(TileValue value) {
        TileValue oppositeValue = value == TileValue.X ? TileValue.O : TileValue.X;

        if (tiles.stream().anyMatch(t -> getValueOf(t) == oppositeValue))
            return false;

        return tiles.stream()
                .filter(this::isEmpty)
                .count() == 2;
    }

    /**
     * @return first empty tile or null if no empty tiles
     */
    public Entity getFirstEmpty() {
        return tiles.stream()
                .filter(this::isEmpty)
                .findAny()
                .orElse(null);
    }

    private TileValue getValueOf(Entity tile) {
        return tile.getComponent(TileViewComponent.class).getValue();
    }

    private boolean isEmpty(Entity tile) {
        return tile.getComponent(TileViewComponent.class).isEmpty();
    }

    public String getWinSymbol() {
        return getValueOf(tile1).symbol;
    }
}
