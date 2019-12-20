package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import static com.almasb.fxglgames.tanks.BattleTanksType.BULLET;
import static com.almasb.fxglgames.tanks.BattleTanksType.ENEMY;

public class BulletEnemyTankHandler extends CollisionHandler {

    public BulletEnemyTankHandler() {
        super(BULLET, ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity tank) {

    }
}
