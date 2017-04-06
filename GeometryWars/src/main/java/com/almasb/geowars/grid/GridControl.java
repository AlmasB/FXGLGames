package com.almasb.geowars.grid;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GridControl extends AbstractControl {

    // TODO: calculate capacity
    private Array<Control> controls = new Array<>(true, 2000);

    @Override
    public void onUpdate(Entity entity, double tpf) {
        for (Control control : controls)
            control.onUpdate(entity, tpf);
    }

    public void addControl(Control control) {
        controls.add(control);
        control.onAdded(getEntity());
    }
}
