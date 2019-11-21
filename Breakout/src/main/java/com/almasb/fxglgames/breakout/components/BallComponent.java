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

package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BallComponent extends Component {

    private static final int BALL_MIN_SPEED = 400;

    private PhysicsComponent physics;
    private EffectComponent effectComponent;

    @Override
    public void onUpdate(double tpf) {
        limitVelocity();
        checkOffscreen();
    }

    private void limitVelocity() {
        // we don't want the ball to move too slow in X direction
        if (abs(physics.getVelocityX()) < BALL_MIN_SPEED) {
            physics.setVelocityX(signum(physics.getVelocityX()) * BALL_MIN_SPEED);
        }

        // we don't want the ball to move too slow in Y direction
        if (abs(physics.getVelocityY()) < BALL_MIN_SPEED) {
            physics.setVelocityY(signum(physics.getVelocityY()) * BALL_MIN_SPEED);
        }
    }

    public void release() {
        physics.setBodyLinearVelocity(new Vec2(5, 5));
    }

    public void grow() {
        effectComponent.startEffect(new GrowEffect());
    }

    // this is a hack:
    // we use a physics engine, so it is possible to push the ball through a wall to outside of the screen
    private void checkOffscreen() {
        if (getEntity().getBoundingBoxComponent().isOutside(getGameScene().getViewport().getVisibleArea())) {
            physics.overwritePosition(new Point2D(
                    getAppWidth() / 2,
                    getAppHeight() / 2
            ));
        }
    }

    public static class GrowEffect extends Effect {

        public GrowEffect() {
            super(Duration.seconds(3));
        }

        @Override
        public void onStart(Entity entity) {
            entity.setScaleX(0.3);
            entity.setScaleY(0.3);
        }

        @Override
        public void onEnd(Entity entity) {
            entity.setScaleX(0.1);
            entity.setScaleY(0.1);
        }
    }
}
