package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.tictactoe.control.TileControl;

/**
 * Instead of using generic GameEntity we add a few convenience methods.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TileEntity extends Entity {

    public TileEntity(double x, double y) {
        setX(x);
        setY(y);
        addComponent(new TileValueComponent());

        getViewComponent().setView(new TileView(this), true);
        addComponent(new TileControl());
    }

    public TileValue getValue() {
        return getComponent(TileValueComponent.class).getValue();
    }

    public TileControl getControl() {
        return getComponent(TileControl.class);
    }
}
