package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

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
