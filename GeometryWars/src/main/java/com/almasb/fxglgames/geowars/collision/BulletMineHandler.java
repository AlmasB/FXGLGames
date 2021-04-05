package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.component.MineComponent;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxglgames.geowars.GeoWarsType.BULLET;
import static com.almasb.fxglgames.geowars.GeoWarsType.MINE;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletMineHandler extends CollisionHandler {

    public BulletMineHandler() {
        super(BULLET, MINE);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity mine) {
        getGameScene().getViewport().shakeTranslational(30);

        mine.getComponent(MineComponent.class).explode();

        spawn("Explosion", new SpawnData(mine.getCenter()).put("numParticles", 10));

        bullet.removeFromWorld();
        mine.removeFromWorld();
    }
}