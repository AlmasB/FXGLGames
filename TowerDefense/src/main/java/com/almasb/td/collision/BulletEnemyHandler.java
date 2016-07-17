package com.almasb.td.collision;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.td.EntityType;
import com.almasb.td.event.EnemyKilledEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletEnemyHandler extends CollisionHandler {

    public BulletEnemyHandler() {
        super(EntityType.BULLET, EntityType.ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity enemy) {

        bullet.removeFromWorld();

        // TODO: add HP/Damage system
        enemy.removeFromWorld();
        FXGL.getEventBus().fireEvent(new EnemyKilledEvent((GameEntity) enemy));
    }
}
