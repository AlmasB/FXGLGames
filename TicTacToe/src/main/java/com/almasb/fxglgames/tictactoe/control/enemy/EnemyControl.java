package com.almasb.fxglgames.tictactoe.control.enemy;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.event.Subscriber;
import com.almasb.fxglgames.tictactoe.event.AIEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class EnemyControl extends Component {

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
