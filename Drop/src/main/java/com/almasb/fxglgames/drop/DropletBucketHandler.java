package com.almasb.fxglgames.drop;

import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;

/**
 * Handler for collisions between a droplet and the bucket.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class DropletBucketHandler extends CollisionHandler {

    public DropletBucketHandler() {
        super(DropType.DROPLET, DropType.BUCKET);
    }

    @Override
    protected void onCollisionBegin(Entity droplet, Entity bucket) {
        droplet.removeFromWorld();

        FXGL.getAudioPlayer().playSound("drop.wav");
    }
}
