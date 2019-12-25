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

package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RunnerComponent extends Component {

    private int screenWidth, screenHeight;

    private double angleAdjustRate = FXGLMath.random(0, 0.5);

    private Vec2 velocity = new Vec2();
    private double directionAngle = FXGLMath.toDegrees(FXGLMath.random(-1, 1) * FXGLMath.PI2);

    private int moveSpeed;
    private int rotationSpeed = FXGLMath.random(-100, 100);

    private float tx = FXGLMath.random(1000, 10000);

    private Entity runner;

    //private EntityGroup<Entity> bullets;

    public RunnerComponent(int moveSpeed) {
        screenWidth = FXGL.getAppWidth();
        screenHeight = FXGL.getAppHeight();
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded() {
        runner = entity;
        //runner.getView().setEffect(new Bloom(0.5));

        //bullets = FXGL.getGameWorld().getGroup(GeoWarsType.BULLET);
    }

    @Override
    public void onUpdate(double tpf) {
        fleeBullets(tpf);
    }

    private int count = 0;

    private void fleeBullets(double tpf) {

        // from nature of code
        float desiredDistance = 50*2;

        Vec2 sum = new Vec2();
        count = 0;

        // check if it's too close
//        bullets.forEach(bullet -> {
//
//            double d = bullet.distance(runner);
//
//            // If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
//            if ((d > 0) && (d < desiredDistance)) {
//                // Calculate vector pointing away from bullet
//                Point2D diff = runner.getCenter().subtract(bullet.getCenter()).normalize().multiply(1 / d);
//
//                sum.addLocal(diff.getX(), diff.getY());
//
//                count++;
//            }
//        });
//
//        // we have a bullet close
//        if (count > 0) {
//            runner.getComponent(RandomMoveControl.class).pause();
//
//            // Our desired vector is moving away
//            sum.normalizeLocal().mulLocal(moveSpeed * tpf);
//
//            runner.translate(sum);
//        } else {
//            runner.getComponent(RandomMoveControl.class).resume();
//        }
    }

    private void adjustAngle(double tpf) {
        if (FXGLMath.randomBoolean(angleAdjustRate)) {
            directionAngle += FXGLMath.toDegrees((FXGLMath.noise1D(tx) - 0.5));
        }
    }

    private void move(double tpf) {
        Vec2 directionVector = Vec2.fromAngle(directionAngle).mulLocal(moveSpeed);

        velocity.addLocal(directionVector).mulLocal((float)tpf);

        runner.translate(new Point2D(velocity.x, velocity.y));
    }

    private void checkScreenBounds() {
        if (runner.getX() < 0
                || runner.getY() < 0
                || runner.getRightX() >= screenWidth
                || runner.getBottomY() >= screenHeight) {

            Point2D newDirectionVector = new Point2D(screenWidth / 2, screenHeight / 2)
                    .subtract(runner.getCenter());

            double angle = Math.toDegrees(Math.atan(newDirectionVector.getY() / newDirectionVector.getX()));
            directionAngle = newDirectionVector.getX() > 0 ? angle : 180 + angle;
        }
    }

    private void rotate(double tpf) {
        runner.rotateBy(rotationSpeed * tpf);
    }
}