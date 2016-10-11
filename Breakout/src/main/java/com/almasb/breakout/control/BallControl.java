package com.almasb.breakout.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.gameutils.math.Vec2;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BallControl extends AbstractControl {

    private PhysicsComponent physics;

    @Override
    public void onAdded(Entity entity) {
        physics = entity.getComponentUnsafe(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        limitVelocity();
    }

    private void limitVelocity() {
        if (Math.abs(physics.getLinearVelocity().getX()) < 5 * 60) {
            physics.setLinearVelocity(Math.signum(physics.getLinearVelocity().getX()) * 5 * 60,
                    physics.getLinearVelocity().getY());
        }

        if (Math.abs(physics.getLinearVelocity().getY()) < 5 * 60) {
            physics.setLinearVelocity(physics.getLinearVelocity().getX(),
                    Math.signum(physics.getLinearVelocity().getY()) * 5 * 60);
        }
    }

    public void release() {
        physics.setBodyLinearVelocity(new Vec2(5, 5));
    }
}
