package com.almasb.fxglgames.tanks.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

import static com.almasb.fxgl.dsl.FXGL.getDialogService;
import static com.almasb.fxgl.dsl.FXGL.getGameController;
import static com.almasb.fxglgames.tanks.BattleTanksType.BULLET;
import static com.almasb.fxglgames.tanks.BattleTanksType.ENEMY_FLAG;

public class BulletFlagHandler extends CollisionHandler {

    public BulletFlagHandler() {
        super(BULLET, ENEMY_FLAG);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity flag) {
        gameOver(flag.isType(ENEMY_FLAG));
    }

    private void gameOver(boolean isEnemyFlagDead) {
        getDialogService().showMessageBox(isEnemyFlagDead ? "Player wins" : "Enemy wins", getGameController()::exit);
    }
}
