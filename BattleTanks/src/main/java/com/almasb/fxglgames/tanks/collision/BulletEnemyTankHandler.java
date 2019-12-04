package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import static com.almasb.fxglgames.tanks.BattleTanksType.*;

public class BulletEnemyTankHandler extends CollisionHandler {

    public BulletEnemyTankHandler() {
        super(BULLET, ENEMY);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity tank) {
        System.out.println("bullet to: " + tank.getType());
    }
}
