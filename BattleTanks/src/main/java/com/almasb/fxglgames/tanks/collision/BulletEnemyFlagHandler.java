package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import static com.almasb.fxglgames.tanks.BattleTanksType.*;

public class BulletEnemyFlagHandler extends CollisionHandler {

    public BulletEnemyFlagHandler() {
        super(BULLET, ENEMY_FLAG);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity flag) {

    }
}
