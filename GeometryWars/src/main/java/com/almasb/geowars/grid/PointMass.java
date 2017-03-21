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

package com.almasb.geowars.grid;

import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PointMass {

    private Point2D position;
    private Point2D velocity = Point2D.ZERO;
    private Point2D acceleration = Point2D.ZERO;

    private final double initialDamping;
    private double damping;
    private double inverseMass;

    public PointMass(Point2D position, double damping, double inverseMass) {
        this.position = position;
        this.damping = damping;
        this.initialDamping = damping;
        this.inverseMass = inverseMass;
    }

    public void applyForce(Point2D force) {
        acceleration = acceleration.add(force.multiply(inverseMass));
    }

    public void increaseDamping(double factor) {
        damping *= factor;
    }

    private void applyAcceleration() {
        velocity = velocity.add(acceleration.multiply(1));
        acceleration = Point2D.ZERO;
    }

    private void applyVelocity() {
        position = position.add(velocity.multiply(0.6));

        if (velocity.magnitude() * velocity.magnitude() < 0.0001) {
            velocity = Point2D.ZERO;
        }

        velocity = velocity.multiply(damping);
    }

    public void update() {
        applyAcceleration();
        applyVelocity();

        damping = initialDamping;
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getVelocity() {
        return velocity;
    }
}
