package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GridComponent extends Component {

    // TODO: calculate capacity
    private Array<Component> controls = new Array<>(2000);

    @Override
    public void onUpdate(double tpf) {
        for (Component control : controls)
            control.onUpdate(tpf);
    }

    public void addComponent(Component control) {
//        controls.add(control);
//        control.onAdded();
    }
}
