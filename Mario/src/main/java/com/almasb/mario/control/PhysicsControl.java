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

package com.almasb.mario.control;

//import com.almasb.fxgl.entity.AbstractControl;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.mario.Physics;
//
//import javafx.geometry.Point2D;
//
///**
// * Allows moving an entity with physics collision
// * rules, including gravity effect
// *
// * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
// * @version 1.0
// *
// */
//public class PhysicsControl extends AbstractControl {
//
//    private Physics physics;
//    private Point2D velocity = new Point2D(0, 0);
//
//    public PhysicsControl(Physics physics) {
//        this.physics = physics;
//    }
//
//    @Override
//    protected void initEntity(Entity entity) {
//        entity.setProperty("jumping", false);
//        entity.setProperty("g", true);
//    }
//
//    @Override
//    public void onUpdate(Entity entity, long now) {
//        if (entity.<Boolean>getProperty("g")) {
//            velocity = velocity.add(0, 1);
//            if (velocity.getY() > 10)
//                velocity = new Point2D(velocity.getX(), 10);
//
//            physics.moveY(entity, (int)velocity.getY());
//        }
//    }
//
//    public boolean moveX(int value) {
//        return physics.moveX(entity, value);
//    }
//
//    public void moveY(int value) {
//        physics.moveY(entity, value);
//    }
//
//    public void jump() {
//        if (entity.<Boolean>getProperty("jumping"))
//            return;
//
//        entity.setProperty("jumping", true);
//        velocity = velocity.add(0, -25);
//    }
//
//    public Point2D getVelocity() {
//        return velocity;
//    }
//}
