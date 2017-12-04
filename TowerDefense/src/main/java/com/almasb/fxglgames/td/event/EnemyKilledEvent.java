package com.almasb.fxglgames.td.event;

import com.almasb.fxgl.entity.Entity;
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

    private Entity enemy;

    /**
     * @return killed enemy
     */
    public Entity getEnemy() {
        return enemy;
    }

    public EnemyKilledEvent(Entity enemy) {
        super(ANY);
        this.enemy = enemy;
    }
}
