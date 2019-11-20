package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private Vec2 acceleration = new Vec2(6, 0);

    @Override
    public void onUpdate(double tpf) {
        acceleration.x += tpf * 0.1;
        acceleration.y += tpf * 10;

        if (acceleration.y < -5)
            acceleration.y = -5;

        if (acceleration.y > 5)
            acceleration.y = 5;

        entity.translate(acceleration.x, acceleration.y);

        if (entity.getBottomY() > getAppHeight()) {
            FXGL.<FlappyBirdApp>getAppCast().requestNewGame();
        }
    }

    public void jump() {
        acceleration.addLocal(0, -5);

        play("jump.wav");
    }
}
