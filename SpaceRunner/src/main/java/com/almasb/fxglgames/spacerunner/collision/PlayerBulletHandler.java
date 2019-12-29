package com.almasb.fxglgames.spacerunner.collision;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

import static com.almasb.fxgl.dsl.FXGL.*;

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

        HealthIntComponent health = player.getComponent(HealthIntComponent.class);

        health.setValue(health.getValue() - 1);

        if (health.getValue() <= 0) {
            // TODO: kill player
        }
    }
}
