package com.almasb.fxglgames.tictactoe.components.enemy;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.event.Subscriber;
import com.almasb.fxglgames.tictactoe.event.AIEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class EnemyComponent extends Component {

    private Subscriber eventListener;

    @Override
    public void onAdded() {
        eventListener = FXGL.getEventBus().addEventHandler(AIEvent.WAITING, event -> {
            makeMove();
            FXGL.getEventBus().fireEvent(new AIEvent(AIEvent.MOVED));
        });
    }

    @Override
    public void onRemoved() {
        eventListener.unsubscribe();
    }

    public abstract void makeMove();
}
