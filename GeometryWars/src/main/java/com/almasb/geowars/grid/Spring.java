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

import com.almasb.fxgl.entity.Entity;

import com.almasb.fxgl.gameplay.GameWorld;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Spring {
    private final PointMass end1;
    private final PointMass end2;

    private final double lengthAtRest;

    private final double stiffness;
    private final double damping;

    public Spring(PointMass end1, PointMass end2, double stiffness, double damping,
                  boolean visible, Entity defaultLine, GameWorld world) {
        this.end1 = end1;
        this.end2 = end2;
        this.stiffness = stiffness;
        this.damping = damping / 10;
        lengthAtRest = end1.getPosition().distance(end2.getPosition()) * 0.95f;

        if (visible) {
            defaultLine.addControl(new LineControl(end1, end2));
            world.addEntity(defaultLine);
        }
    }

    public void update() {
        Point2D currentVector = end1.getPosition().subtract(end2.getPosition());
        double currentLength = currentVector.magnitude();

        if (currentLength > lengthAtRest) {
            Point2D dv = end2.getVelocity().subtract(end1.getVelocity());

            Point2D force = currentVector.normalize()
                    .multiply(currentLength - lengthAtRest)
                    .multiply(stiffness)
                    .subtract(dv.multiply(damping));

            end1.applyForce(force.multiply(-1));
            end2.applyForce(force);
        }
    }
}
