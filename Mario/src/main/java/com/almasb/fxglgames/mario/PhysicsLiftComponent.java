/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.mario;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * Moves the entity up and down.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PhysicsLiftComponent extends Component {

    private PhysicsComponent physics;

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private boolean goingUp;
    private double speed;

    /**
     * Constructs lift component (moving vertically).
     *
     * @param duration duration going (one way)
     * @param distance the distance entity travels (one way)
     * @param goingUp if true, entity starts going up, otherwise down
     */
    public PhysicsLiftComponent(Duration duration, double distance, boolean goingUp) {
        this.duration = duration;
        this.distance = distance;
        this.goingUp = goingUp;
    }

    @Override
    public void onAdded() {
        timer = FXGL.newLocalTimer();
        timer.capture();
        speed = distance / duration.toSeconds();

        physics.setOnPhysicsInitialized(() -> {
            physics.setVelocityY(goingUp ? -speed : speed);
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(duration)) {
            goingUp = !goingUp;
            timer.capture();

            physics.setVelocityY(goingUp ? -speed : speed);
        }
    }
}
