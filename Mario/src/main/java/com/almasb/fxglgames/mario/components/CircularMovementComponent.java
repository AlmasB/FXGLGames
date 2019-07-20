package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

/**
 * Moves entity in a circle.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CircularMovementComponent extends Component {

    private double radius;
    private double speed;
    private double t = 0.0;

    private Entity player;

    public CircularMovementComponent(double speed, double radius) {
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) {
            player = FXGL.geto("player");
        }

        // TODO: generalize player -> entity
        double x = player.getX() - Math.cos(t) * radius;
        double y = player.getY() - Math.sin(t) * radius;

        t += tpf * speed;

        entity.setX(x + Math.cos(t) * radius);
        entity.setY(y + Math.sin(t) * radius);
    }
}
