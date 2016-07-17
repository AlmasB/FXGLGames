package com.almasb.td.event;

import com.almasb.fxgl.entity.GameEntity;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Occurs when an enemy is killed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyKilledEvent extends Event {

    public static final EventType<EnemyKilledEvent> ANY
            = new EventType<>(Event.ANY, "ENEMY_KILLED");

    private GameEntity enemy;

    /**
     * @return killed enemy
     */
    public GameEntity getEnemy() {
        return enemy;
    }

    public EnemyKilledEvent(GameEntity enemy) {
        super(ANY);
        this.enemy = enemy;
    }
}
