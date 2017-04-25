package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.ecs.component.ObjectComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileValueComponent extends ObjectComponent<TileValue> {

    public TileValueComponent() {
        super(TileValue.NONE);
    }
}
