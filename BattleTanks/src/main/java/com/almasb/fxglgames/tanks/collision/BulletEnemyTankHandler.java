package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
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
        bullet.removeFromWorld();

        var hp = tank.getComponent(HealthIntComponent.class);

        hp.damage(1);

        if (hp.isZero()) {
            tank.removeFromWorld();
        }
    }
}
