package com.almasb.fxglgames.gravity.old;

import com.almasb.fxgl.core.math.Vec2;
import javafx.scene.shape.Rectangle;

import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

public class Bullet extends GameObject {

    private int tick = 0;

    public Bullet(float x, float y, Vec2 velocity) {
        super(x, y, 5, 1, BodyType.DYNAMIC, true);
        body.setBullet(true);
        body.setGravityScale(0.0f);

        getChildren().add(new Rectangle(5, 1));

        body.setLinearVelocity(velocity);
    }

    @Override
    public void onUpdate() {
        if (dying) {
            if (!bodyDestroyed) {
                GameEnvironment.getWorld().destroyBody(body);
                bodyDestroyed = true;
                alive = false;
            }
            return;
        }

        tick++;
        if (tick == 3 * Config.SECOND)
            onDeath();
    }

    @Override
    public void onDeath() {
        dying = true;
    }

    @Override
    public void onCollide(GameObject other) {
        onDeath();
    }

    @Override
    public Type getType() {
        return Type.BULLET;
    }
}
