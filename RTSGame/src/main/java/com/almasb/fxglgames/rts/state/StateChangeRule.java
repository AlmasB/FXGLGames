package com.almasb.fxglgames.rts.state;

import java.util.function.Supplier;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class StateChangeRule {

    private EntityState newState;
    private Supplier<Boolean> condition;

    public StateChangeRule(EntityState newState, Supplier<Boolean> condition) {
        this.newState = newState;
        this.condition = condition;
    }

    public EntityState getNewState() {
        return newState;
    }

    public Supplier<Boolean> getCondition() {
        return condition;
    }
}
