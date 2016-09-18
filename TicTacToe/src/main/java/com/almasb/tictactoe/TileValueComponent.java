package com.almasb.tictactoe;

import com.almasb.ents.component.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileValueComponent extends ObjectComponent<TileValue> {

    public TileValueComponent() {
        super(TileValue.NONE);
    }
}
