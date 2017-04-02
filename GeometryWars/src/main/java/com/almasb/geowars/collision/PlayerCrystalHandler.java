package com.almasb.geowars.collision;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.geowars.GeoWarsType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class PlayerCrystalHandler extends CollisionHandler {

    public PlayerCrystalHandler() {
        super(GeoWarsType.PLAYER, GeoWarsType.CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity crystal) {
        crystal.removeFromWorld();

        FXGL.getApp().getGameState().increment("multiplier", +1);
    }
}
