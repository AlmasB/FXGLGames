package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletComponent extends Component {

    private Entity lastPortal = null;

    public Entity getLastPortal() {
        return lastPortal;
    }

    public void setLastPortal(Entity lastPortal) {
        this.lastPortal = lastPortal;
    }
}
