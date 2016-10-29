package com.almasb.breakout.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.gameutils.math.Vec2;
import com.almasb.gameutils.reflect.Field;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BatControl extends AbstractControl {

    private static final float BOUNCE_FACTOR = 1.5f;
    private static final float SPEED_DECAY = 0.66f;

    private GameEntity bat;
    private PhysicsComponent physics;
    private float speed = 0;

    private Vec2 velocity = new Vec2();

    @Override
    public void onAdded(Entity entity) {
        bat = (GameEntity) entity;
        physics = Entities.getPhysics(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = 600 * (float)tpf;

        velocity.mulLocal(SPEED_DECAY);

        if (bat.getX() < 0) {
            velocity.set(BOUNCE_FACTOR * (float) -bat.getX(), 0);
        } else if (bat.getRightX() > FXGL.getApp().getWidth()) {
            velocity.set(BOUNCE_FACTOR * (float) -(bat.getRightX() - FXGL.getApp().getWidth()), 0);
        }

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
    }

    public void right() {
        velocity.set(speed, 0);
    }
}
