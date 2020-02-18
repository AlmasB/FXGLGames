package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.tanks.BattleTanksType;

public class BulletBrickHandler extends CollisionHandler {

    public BulletBrickHandler() {
        super(BattleTanksType.BULLET, BattleTanksType.BRICK);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity brick) {
        bullet.removeFromWorld();
        brick.call("onHit");
    }
}
