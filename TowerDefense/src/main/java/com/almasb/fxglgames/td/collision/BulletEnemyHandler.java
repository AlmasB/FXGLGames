package com.almasb.fxglgames.td.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.components.TowerComponent;
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
        Entity tower = bullet.getObject("tower");
        TowerComponent data = tower.getComponent(TowerComponent.class);

        bullet.removeFromWorld();

        var hp = enemy.getComponent(HealthIntComponent.class);

        hp.damage(data.getDamage());

        if (hp.isZero()) {
            FXGL.getEventBus().fireEvent(new EnemyKilledEvent(enemy));
            enemy.removeFromWorld();
        }
    }
}
