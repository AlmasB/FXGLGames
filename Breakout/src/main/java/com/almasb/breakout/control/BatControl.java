package com.almasb.breakout.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.gameutils.math.Vec2;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BatControl extends AbstractControl {

    private PhysicsComponent physics;
    private float speed = 0;

    private Vec2 velocity = new Vec2();

    @Override
    public void onAdded(Entity entity) {
        physics = Entities.getPhysics(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = 600 * (float)tpf;

        velocity.mulLocal(0.66f);

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
    }

    public void right() {
        velocity.set(speed, 0);
    }
}
