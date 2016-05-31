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

//import com.almasb.fxgl.entity.Control;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.time.TimerManager;
//import com.almasb.mario.EntityType.ProjectileType;
//import com.almasb.mario.EntityType.Property;
//import com.almasb.mario.EntityType.Type;
//
//import javafx.geometry.Point2D;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.util.Duration;
//
//public class EnemyPlantControl extends EnemyControl {
//
//    private Entity player;
//
//    public EnemyPlantControl(Entity player) {
//        this.player = player;
//    }
//
//    @Override
//    public void onUpdate(Entity entity, long now) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    protected void initEntity(Entity entity) {
//        entity.addControl(new AIProximityControl(player));
//    }
//
//    @Override
//    public Entity makeAttack() {
//        Entity e = new Entity(Type.PROJECTILE);
//        e.setProperty(Property.SUB_TYPE, ProjectileType.ENEMY);
//        e.setPosition(entity.getPosition().add(15, 5));
//
//        Circle graphics = new Circle(15);
//        graphics.setFill(Color.GREEN);
//        e.setGraphics(graphics);
//
//        e.setCollidable(true);
//        e.addControl(new Control() {
//            private Point2D v = player.getCenter().subtract(e.getCenter()).normalize().multiply(5);
//
//            @Override
//            public void onUpdate(Entity entity, long now) {
//                entity.translate(v);
//            }
//        });
//        e.setExpireTime(Duration.seconds(2));
//
//        return e;
//    }
//
//}
