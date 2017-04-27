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

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WandererControl extends AbstractControl {

    private int screenWidth, screenHeight;

    private float angleAdjustRate = FXGLMath.random(0, 0.5f);

    private Vec2 velocity = new Vec2();
    private double directionAngle = FXGLMath.random(-1, 1) * FXGLMath.PI2 * FXGLMath.radiansToDegrees;

    private int moveSpeed;
    private int rotationSpeed = FXGLMath.random(-100, 100);

    private float tx = FXGLMath.random(1000, 10000);

    private GameEntity wanderer;

    public WandererControl(int moveSpeed) {
        screenWidth = FXGL.getApp().getWidth();
        screenHeight = FXGL.getApp().getHeight();
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded(Entity entity) {
        wanderer = (GameEntity) entity;
        wanderer.getView().setEffect(new Bloom(0.5));
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        adjustAngle(tpf);
        move(tpf);
        rotate(tpf);

        tx += tpf;

        checkScreenBounds();
    }

    private void adjustAngle(double tpf) {
        if (FXGLMath.randomBoolean(angleAdjustRate)) {
            directionAngle += FXGLMath.radiansToDegrees * (FXGLMath.noise1D(tx) - 0.5);
        }
    }

    private void move(double tpf) {
        Vec2 directionVector = Vec2.fromAngle(directionAngle).mulLocal(moveSpeed);

        velocity.addLocal(directionVector).mulLocal((float)tpf);

        wanderer.translate(new Point2D(velocity.x, velocity.y));
    }

    private void checkScreenBounds() {
        if (wanderer.getX() < 0
                || wanderer.getY() < 0
                || wanderer.getRightX() >= screenWidth
                || wanderer.getBottomY() >= screenHeight) {

            Point2D newDirectionVector = new Point2D(screenWidth / 2, screenHeight / 2)
                    .subtract(wanderer.getCenter());

            double angle = Math.toDegrees(Math.atan(newDirectionVector.getY() / newDirectionVector.getX()));
            directionAngle = newDirectionVector.getX() > 0 ? angle : 180 + angle;
        }
    }

    private void rotate(double tpf) {
        wanderer.rotateBy(rotationSpeed * tpf);
    }
}