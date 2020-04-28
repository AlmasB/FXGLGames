package com.almasb.fxglgames.rts.state;

import com.almasb.fxgl.core.fsm.State;

/**
 * A state in which an entity can be in (via StateComponent).
 * Subclasses may allow concurrency and substates.
 * 
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface EntityState extends State<EntityState> {

    EntityState IDLE = tpf -> { };
    
    @Override
    default boolean isAllowConcurrency() {
        return false;
    }

    @Override
    default boolean isSubState() {
        return false;
    }

    @Override
    default void onCreate() {

    }

    @Override
    default void onDestroy() {

    }

    @Override
    default void onEnteredFrom(EntityState entityState) {

    }

    @Override
    default void onExitingTo(EntityState entityState) {

    }

    void onUpdate(double tpf);
}
