package com.almasb.fxglgames.spacerunner.collision;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;
import com.almasb.fxglgames.spacerunner.ai.SquadAI;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletAIPointHandler extends CollisionHandler {
    public BulletAIPointHandler() {
        super(SpaceRunnerType.BULLET, SpaceRunnerType.AI_POINT);
    }

    @Override
    protected void onCollisionBegin(Entity a, Entity point) {
        SquadAI.INSTANCE.collisionWithBulletBegin(a, point);
    }


    @Override
    protected void onCollisionEnd(Entity a, Entity point) {
        SquadAI.INSTANCE.collisionWithBulletEnd(a, point);
    }
}
