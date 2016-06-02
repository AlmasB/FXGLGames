/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.mario.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private static final int MAX_SPEED = 5;

    private Point2D acceleration = Point2D.ZERO;

    private PhysicsComponent physics;

    @Override
    public void onAdded(Entity entity) {
        physics = entity.getComponentUnsafe(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        physics.setLinearVelocity(physics.getLinearVelocity().add(acceleration));

        limitVelocity();

        acceleration = Point2D.ZERO;
    }

    private void limitVelocity() {
        Point2D vel = physics.getLinearVelocity();

        if (vel.getX() < -MAX_SPEED)
            vel = new Point2D(-MAX_SPEED, vel.getY());

        if (vel.getX() > MAX_SPEED)
            vel = new Point2D(MAX_SPEED, vel.getY());

        if (vel.getY() < -MAX_SPEED)
            vel = new Point2D(vel.getX(), -MAX_SPEED);

        if (vel.getY() > MAX_SPEED)
            vel = new Point2D(vel.getX(), MAX_SPEED);

        physics.setLinearVelocity(vel);
    }

    public void jump() {
        acceleration = acceleration.add(0, -10);
    }

    public void right() {
        acceleration = acceleration.add(1, 0);
    }

    public void left() {
        acceleration = acceleration.add(-1, 0);
    }
}
