package com.almasb.fxglgames.geowars.event;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DeathEvent extends Event {

    public static final EventType<DeathEvent> ANY = new EventType<>(EventType.ROOT, "DEATH_EVENT");

    private Entity entity;

    public DeathEvent(Entity entity) {
        super(ANY);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isPlayer() {
        return entity.isType(GeoWarsType.PLAYER);
    }

    public boolean isEnemy() {
        return entity.isType(GeoWarsType.WANDERER)
                || entity.isType(GeoWarsType.SEEKER)
                || entity.isType(GeoWarsType.BOUNCER)
                || entity.isType(GeoWarsType.RUNNER);
    }
}
