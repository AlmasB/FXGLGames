package com.almasb.tictactoe.control.enemy;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxeventbus.Subscriber;
import com.almasb.fxgl.app.FXGL;
import com.almasb.tictactoe.event.AIEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class EnemyControl extends AbstractControl {

    private Subscriber eventListener;

    @Override
    public void onAdded(Entity entity) {
        eventListener = FXGL.getEventBus().addEventHandler(AIEvent.WAITING, event -> {
            makeMove();
            FXGL.getEventBus().fireEvent(new AIEvent(AIEvent.MOVED));
        });
    }

    @Override
    public void onRemoved(Entity entity) {
        eventListener.unsubscribe();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {}

    public abstract void makeMove();
}
