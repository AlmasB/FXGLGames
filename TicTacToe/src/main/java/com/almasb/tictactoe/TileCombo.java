package com.almasb.tictactoe;

import java.util.Arrays;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileCombo {

    private TileEntity tile1, tile2, tile3;
    private List<TileEntity> tiles;

    public TileCombo(TileEntity tile1, TileEntity tile2, TileEntity tile3) {
        this.tile1 = tile1;
        this.tile2 = tile2;
        this.tile3 = tile3;

        tiles = Arrays.asList(tile1, tile2, tile3);
    }

    public TileEntity getTile1() {
        return tile1;
    }

    public TileEntity getTile2() {
        return tile2;
    }

    public TileEntity getTile3() {
        return tile3;
    }

    public boolean isComplete() {
        return tile1.getValue() != TileValue.NONE
                && tile1.getValue() == tile2.getValue()
                && tile1.getValue() == tile3.getValue();
    }

    public boolean isOpenHalfComboX() {
        if (tiles.stream().anyMatch(t -> t.getValue() == TileValue.O))
            return false;

        // TODO: complete

        return true;
    }

    public String getWinSymbol() {
        return tile1.getValue().symbol;
    }
}
