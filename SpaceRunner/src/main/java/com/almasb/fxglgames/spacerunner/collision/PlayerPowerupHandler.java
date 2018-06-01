package com.almasb.fxglgames.spacerunner.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;
import com.almasb.fxglgames.spacerunner.components.PowerupComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerPowerupHandler extends CollisionHandler {

    public PlayerPowerupHandler() {
        super(SpaceRunnerType.PLAYER, SpaceRunnerType.POWERUP);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity powerup) {
        powerup.getComponent(PowerupComponent.class).onCollisionBegin(player);
    }
}
