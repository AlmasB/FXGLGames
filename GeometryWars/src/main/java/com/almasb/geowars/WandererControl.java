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

package com.almasb.geowars;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.Entities;
import javafx.geometry.Point2D;

import java.util.Random;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WandererControl extends AbstractControl {

    private int screenWidth, screenHeight;

    private Point2D velocity;
    private double directionAngle;

    public WandererControl(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        velocity = new Point2D(0, 0);
        directionAngle = new Random().nextFloat() * Math.PI * 2f;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        // change the directionAngle a bit
        directionAngle += (new Random().nextFloat() * 20f - 10f) * tpf;
        Point2D directionVector = getVectorFromAngle(directionAngle);
        directionVector = directionVector.multiply(1000f);
        velocity = velocity.add(directionVector);

        // decrease the velocity a bit and move the wanderer
        velocity = velocity.multiply(0.65f);
        Entities.getPosition(entity).translate(velocity.multiply(tpf * 0.1f));

        // make the wanderer bounce off the screen borders
        Point2D loc = Entities.getPosition(entity).getValue();
        if (loc.getX() < 0 || loc.getY() < 0 || loc.getX() + 40 >= screenWidth
                || loc.getY() + 40 >= screenHeight) {
            Point2D newDirectionVector = new Point2D(screenWidth / 2, screenHeight / 2).subtract(loc);

            double angle = Math.toDegrees(Math.atan(newDirectionVector.getY() / newDirectionVector.getX()));
            angle = newDirectionVector.getX() > 0 ? angle : 180 + angle;

            directionAngle = Math.toRadians(angle);
        }

        // rotate the wanderer
        Entities.getRotation(entity).rotateBy(0.016 * 2);
    }

    private Point2D getVectorFromAngle(double angle) {
        return new Point2D(Math.cos(angle), Math.sin(angle));
    }
}