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

package com.almasb.geowars.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.MainViewComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleControl extends AbstractControl {

    private Point2D velocity;
    private float lifespan;
    private long spawnTime;
    private Color color;

    public ParticleControl(Point2D velocity, float lifespan, Color color) {
        this.velocity = velocity;
        this.lifespan = lifespan;
        this.color = color;
        spawnTime = System.currentTimeMillis();
    }

    @Override
    public void onAdded(Entity entity) {
        entity.getComponent(MainViewComponent.class).ifPresent(view -> view.getView().setEffect(new DropShadow(5, color)));
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
//        // movement
//        entity.translate(velocity.multiply(0.016 * 3f));
//        velocity = velocity.multiply(1 - 3f * 0.016);
//        if (Math.abs(velocity.getX()) + Math.abs(velocity.getY()) < 0.001f) {
//            velocity = new Point2D(0, 0);
//        }
//
//        // rotation and scale
//        if (velocity.getX() != 0 && velocity.getY() != 0) {
//            entity.rotateToVector(velocity);
//        }
//
//        // alpha
//        double speed = velocity.magnitude();
//        long difTime = System.currentTimeMillis() - spawnTime;
//        float percentLife = 1 - difTime / lifespan;
//        double alpha = lesserValue(1.5f, lesserValue(percentLife * 2, speed));
//        alpha *= alpha;
//
//        final double opacity = alpha;
//        entity.getSceneView().ifPresent(view -> view.setOpacity(opacity));
//
//        // is particle expired?
//        if (difTime > lifespan) {
//            entity.removeFromWorld();
//        }
    }

    private double lesserValue(double a, double b) {
        return a < b ? a : b;
    }
//
//    public void applyGravity(Vector3f gravity, float distance) {
//        Vector3f additionalVelocity = gravity
//                .mult(1000f / (distance * distance + 10000f));
//        velocity.addLocal(additionalVelocity);
//
//        if (distance < 400) {
//            additionalVelocity = new Vector3f(gravity.y, -gravity.x, 0)
//                    .mult(3f / (distance + 100));
//            velocity.addLocal(additionalVelocity);
//        }
//    }
}