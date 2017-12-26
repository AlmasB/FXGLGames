package com.almasb.fxglgames.mario;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Control {

    private PhysicsComponent physics;

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void left() {
        physics.setVelocityX(-150);
    }

    public void right() {
        physics.setVelocityX(150);
    }

    public void jump() {
        physics.setVelocityY(-400);
    }
}
