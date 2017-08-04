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

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsControl;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BallControl extends Control {

    private PhysicsComponent ball;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        limitVelocity();
        checkOffscreen();
    }

    private void limitVelocity() {
        if (Math.abs(ball.getLinearVelocity().getX()) < 5 * 60) {
            ball.setLinearVelocity(Math.signum(ball.getLinearVelocity().getX()) * 5 * 60,
                    ball.getLinearVelocity().getY());
        }

        if (Math.abs(ball.getLinearVelocity().getY()) > 5 * 60 * 2) {
            ball.setLinearVelocity(ball.getLinearVelocity().getX(),
                    Math.signum(ball.getLinearVelocity().getY()) * 5 * 60);
        }
    }

    // this is a hack:
    // we use a physics engine, so it is possible to push the ball against a wall
    // so that it gets moved outside of the screen
    private void checkOffscreen() {
        if (getEntity().getComponent(BoundingBoxComponent.class).isOutside(FXGL.getApp()
                .getGameScene()
                .getViewport()
                .getVisibleArea())) {

            getEntity().getControl(PhysicsControl.class).reposition(new Point2D(
                    FXGL.getAppWidth() / 2,
                    FXGL.getAppHeight() / 2
            ));
        }
    }
}
