package com.almasb.tictactoe;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileCombo {

    private Tile tile1, tile2, tile3;

    public TileCombo(Tile tile1, Tile tile2, Tile tile3) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;
    }

    public Tile getTile1() {
        return tile1;
    }

    public Tile getTile2() {
        return tile2;
    }

    public Tile getTile3() {
        return tile3;
    }

    public boolean isComplete() {
        return tile1.getValue() != TileValue.NONE
                && tile1.getValue() == tile2.getValue()
                && tile1.getValue() == tile3.getValue();

    }

    public String getWinSymbol() {
        return tile1.getValue().symbol;
    }
}
