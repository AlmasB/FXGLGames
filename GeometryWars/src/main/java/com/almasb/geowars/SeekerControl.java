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

import javafx.geometry.Point2D;

import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SeekerControl extends AbstractControl {

    private Entity player;
    private Point2D velocity;

    public SeekerControl(Entity player) {
        this.player = player;
        velocity = new Point2D(0, 0);
    }

    @Override
    protected void initEntity(Entity entity) {}

    @Override
    public void onUpdate(Entity entity) {
        // translate the seeker
        Point2D playerDirection = player.getPosition()
                .subtract(entity.getPosition())
                .normalize()
                .multiply(1000);

        velocity = velocity.add(playerDirection)
                .multiply(0.65);

        entity.translate(velocity.multiply(0.016 * 0.1f));

        // rotate the seeker
        if (velocity.getX() != 0 && velocity.getY() != 0) {
            entity.rotateToVector(velocity);
        }
    }
}
