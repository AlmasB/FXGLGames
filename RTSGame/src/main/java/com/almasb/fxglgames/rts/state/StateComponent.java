package com.almasb.fxglgames.rts.state;

import com.almasb.fxgl.core.fsm.StateMachine;
import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class StateComponent extends Component {

    private StateMachine<EntityState> fsm;

    public StateComponent() {
        this(EntityState.IDLE);
    }

    public StateComponent(EntityState initialState) {
        fsm = new StateMachine<>(initialState);
    }

    @Override
    public void onUpdate(double tpf) {
        var state = fsm.getCurrentState();

        state.getRules()
                .stream()
                .filter(rule -> rule.getCondition().get())
                .findFirst()
                .ifPresent(rule -> {
                    changeState(rule.getNewState());
                });

        fsm.getCurrentState().onUpdate(tpf);
    }

    public void changeStateToIdle() {
        changeState(EntityState.IDLE);
    }

    public void changeState(EntityState state) {
        fsm.changeState(state);
    }
}
