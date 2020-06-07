package com.almasb.fxglgames.mario.collisions;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.mario.MarioType;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerButtonHandler extends CollisionHandler {

    public PlayerButtonHandler() {
        super(MarioType.PLAYER, MarioType.BUTTON);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity btn) {
        Entity keyEntity = btn.getObject("keyEntity");

        if (!keyEntity.isActive()) {
            keyEntity.setProperty("activated", false);
            getGameWorld().addEntity(keyEntity);
        }

        keyEntity.setOpacity(1);
    }

    @Override
    protected void onCollisionEnd(Entity player, Entity btn) {
        Entity keyEntity = btn.getObject("keyEntity");
        if (!keyEntity.getBoolean("activated")) {
            keyEntity.setOpacity(0);
        }
    }
}
