package com.almasb.fxglgames.spacerunner.collision;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

import static com.almasb.fxgl.app.DSLKt.getb;
import static com.almasb.fxgl.app.DSLKt.set;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerBulletHandler extends CollisionHandler {

    public PlayerBulletHandler() {
        super(SpaceRunnerType.PLAYER, SpaceRunnerType.ENEMY_BULLET);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity bullet) {
        bullet.removeFromWorld();

        if (getb("hasShield")) {
            set("shield", 0);
            return;
        }

        HealthComponent health = player.getComponent(HealthComponent.class);

        health.setValue(health.getValue() - 5);

        if (health.getValue() <= 0) {
            // TODO: kill player
        }
    }
}
