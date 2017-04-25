package com.almasb.fxglgames.td.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyReachedGoalEvent extends Event {

    public static final EventType<EnemyReachedGoalEvent> ANY
            = new EventType<>(Event.ANY, "EnemyReachedGoalEvent");

    public EnemyReachedGoalEvent() {
        super(ANY);
    }
}