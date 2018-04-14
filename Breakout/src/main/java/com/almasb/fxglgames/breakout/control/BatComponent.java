/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.breakout.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BatComponent extends Component {

    private static final float BOUNCE_FACTOR = 1.5f;
    private static final float SPEED_DECAY = 0.66f;

    private PhysicsComponent physics;
    private float speed = 0;

    private Vec2 velocity = new Vec2();

    @Override
    public void onUpdate(double tpf) {
        speed = 600 * (float)tpf;

        velocity.mulLocal(SPEED_DECAY);

        if (entity.getX() < 0) {
            velocity.set(BOUNCE_FACTOR * (float) -entity.getX(), 0);
        } else if (entity.getRightX() > FXGL.getApp().getWidth()) {
            velocity.set(BOUNCE_FACTOR * (float) -(entity.getRightX() - FXGL.getApp().getWidth()), 0);
        }

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
    }

    public void right() {
        velocity.set(speed, 0);
    }
}
