package com.almasb.fxglgames.td.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.event.EnemyKilledEvent;

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
        FXGL.getEventBus().fireEvent(new EnemyKilledEvent(enemy));
        enemy.removeFromWorld();
    }
}
