package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.ecs.Component;
import com.almasb.fxgl.entity.GameEntity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletComponent extends Component {

    private GameEntity lastPortal = null;

    public GameEntity getLastPortal() {
        return lastPortal;
    }

    public void setLastPortal(GameEntity lastPortal) {
        this.lastPortal = lastPortal;
    }
}
