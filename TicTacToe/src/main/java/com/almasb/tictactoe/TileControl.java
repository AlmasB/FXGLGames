package com.almasb.tictactoe;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(TileValueComponent.class)
public class TileControl extends AbstractControl {

    @Override
    public void onUpdate(Entity entity, double tpf) {}

    /**
     * @param value tile value
     * @return true if marking succeeded
     */
    public boolean mark(TileValue value) {
        TileValueComponent valueComponent = getEntity().getComponentUnsafe(TileValueComponent.class);

        if (valueComponent.getValue() != TileValue.NONE)
            return false;

        valueComponent.setValue(value);

        return true;
    }
}
