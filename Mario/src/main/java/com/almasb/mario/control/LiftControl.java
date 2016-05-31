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

//import com.almasb.fxgl.GameApplication;
//import com.almasb.fxgl.entity.AbstractControl;
//import com.almasb.fxgl.entity.Entity;
//
//public class LiftControl extends AbstractControl {
//
//    private double spawnY;
//    private boolean movingUp = true;
//
//    public LiftControl(double spawnY) {
//        this.spawnY = spawnY;
//    }
//
//    @Override
//    public void onUpdate(Entity entity, long now) {
//        entity.translate(0, movingUp ? -1 : 1);
//
//        if (Math.abs(entity.getPosition().getY() - spawnY) > 90) {
//            movingUp = !movingUp;
//        }
//    }
//
//
//    @Override
//    protected void initEntity(Entity entity) {
//        // TODO Auto-generated method stub
//
//    }
//}
