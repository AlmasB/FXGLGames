package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.app.FXGL;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Control {

    private Vec2 acceleration = new Vec2(6, 0);

    @Override
    public void onUpdate(Entity entity, double tpf) {
        acceleration.x += tpf * 0.1;
        acceleration.y += tpf * 10;

        if (acceleration.y < -5)
            acceleration.y = -5;

        if (acceleration.y > 5)
            acceleration.y = 5;

        entity.translate(acceleration.x, acceleration.y);

        if (entity.getBottomY() > FXGL.getAppHeight()) {
            FXGL.<FlappyBirdApp>getAppCast().requestNewGame();
        }
    }

    public void jump() {
        acceleration.addLocal(0, -5);

        FXGL.getAudioPlayer().playSound("jump.wav");
    }
}
