package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.entity.component.Component;

public class BrickComponent extends Component {

    private int hp = 10;

    public void onHit() {
        if (hp <= 0)
            return;

        hp--;

        if (hp >= 1) {
            entity.getViewComponent().setOpacity(0.5);
        } else {
            entity.removeFromWorld();
        }
    }
}
