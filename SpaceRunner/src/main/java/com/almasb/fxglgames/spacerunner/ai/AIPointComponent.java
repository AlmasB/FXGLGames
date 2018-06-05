package com.almasb.fxglgames.spacerunner.ai;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AIPointComponent extends Component {

    private Entity occupiedBy = null;

    public boolean isOccupied() {
        return occupiedBy != null;
    }

    public Entity getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(Entity occupiedBy) {
        this.occupiedBy = occupiedBy;
    }

    @Override
    public void onUpdate(double tpf) {
        if (occupiedBy != null && !occupiedBy.isActive()) {
            occupiedBy = null;
        }
    }
}
