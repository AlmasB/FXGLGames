package com.almasb.fxglgames.tictactoe.ai;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.event.Subscriber;
import com.almasb.fxglgames.tictactoe.event.AIEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class TicTacToeAIService extends EngineService {

    private Subscriber eventListener;

    @Override
    public void onInit() {
        eventListener = FXGL.getEventBus().addEventHandler(AIEvent.WAITING, event -> {
            makeMove();
            FXGL.getEventBus().fireEvent(new AIEvent(AIEvent.MOVED));
        });
    }

    @Override
    public void onExit() {
        eventListener.unsubscribe();
    }

    public abstract void makeMove();
}
