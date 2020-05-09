package com.almasb.fxglgames.rts;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ResourceComponent extends Component {

    private ResourceType type;
    private int quantity;

    public ResourceComponent(ResourceType type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    public ResourceType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void gather() {
        quantity--;
    }

    public boolean isEmpty() {
        return quantity == 0;
    }
}
