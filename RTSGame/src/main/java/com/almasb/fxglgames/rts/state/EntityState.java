package com.almasb.fxglgames.rts.state;

import com.almasb.fxgl.core.fsm.State;

import java.util.ArrayList;
import java.util.List;

/**
 * A state in which an entity can be in (via StateComponent).
 * 
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class EntityState implements State<EntityState> {

    public static final EntityState IDLE = new EntityState() {
        @Override
        protected void onUpdate(double tpf) {
        }
    };

    private List<StateChangeRule> rules = new ArrayList<>();

    List<StateChangeRule> getRules() {
        return rules;
    }

    public final void addRule(StateChangeRule rule) {
        rules.add(rule);
    }
    
    @Override
    public final boolean isAllowConcurrency() {
        return false;
    }

    @Override
    public final boolean isSubState() {
        return false;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onEnteredFrom(EntityState entityState) {

    }

    @Override
    public void onExitingTo(EntityState entityState) {

    }

    protected abstract void onUpdate(double tpf);
}
