package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveDownComponent extends Component {
    private double speed;

    public MoveDownComponent(double speed) {
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        entity.translateY(speed * tpf);
    }
}