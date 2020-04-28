package com.almasb.fxglgames.rts;

import com.almasb.fxgl.core.fsm.StateMachine;
import com.almasb.fxgl.entity.component.Component;
import kotlin.Unit;

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
        fsm.runOnActiveStates(e -> {
            e.onUpdate(tpf);

            // TODO: update FXGL API to make it easier for java users
            return Unit.INSTANCE;
        });
    }

    public void changeState(EntityState state) {
        fsm.changeState(state);
    }
}
