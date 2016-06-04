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

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.mario.type.EntityType;
import com.almasb.mario.event.DeathEvent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private static final int MAX_SPEED = 5;

    private Point2D acceleration = Point2D.ZERO;

    private PhysicsComponent physics;
    private BoundingBoxComponent bbox;

    private double tpf;

    @Override
    public void onAdded(Entity entity) {
        bbox = entity.getComponentUnsafe(BoundingBoxComponent.class);
        physics = entity.getComponentUnsafe(PhysicsComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        this.tpf = tpf;

        physics.setLinearVelocity(physics.getLinearVelocity().add(acceleration));

        limitVelocity();

        acceleration = acceleration.multiply(0.25);

        //acceleration = Point2D.ZERO;

        if (bbox.getMaxYWorld() > FXGL.getSettings().getHeight()) {
            FXGL.getEventBus().fireEvent(new DeathEvent(DeathEvent.ANY));
        }
    }

    private void limitVelocity() {
        Point2D vel = physics.getLinearVelocity();

        if (vel.getX() < -MAX_SPEED)
            vel = new Point2D(-MAX_SPEED, vel.getY());

        if (vel.getX() > MAX_SPEED)
            vel = new Point2D(MAX_SPEED, vel.getY());

        if (vel.getY() < -MAX_SPEED)
            vel = new Point2D(vel.getX(), -MAX_SPEED);

        if (vel.getY() > MAX_SPEED)
            vel = new Point2D(vel.getX(), MAX_SPEED);

        physics.setLinearVelocity(vel);
    }

    private boolean canJump() {
        double minX = bbox.getMinXWorld();
        double maxY = bbox.getMaxYWorld();

        return FXGL.getGame()
                .getGameWorld()
                .getEntitiesByType(EntityType.PLATFORM)
                .stream()
                .filter(e -> Entities.getBBox(e).isWithin(new Rectangle2D(minX, maxY, bbox.getWidth(), 5)))
                .findAny()
                .isPresent();
    }

    public void jump() {
        if (canJump())
            acceleration = acceleration.add(0, -10);
    }

    public void right() {
        acceleration = acceleration.add(10 * tpf, 0);
    }

    public void left() {
        acceleration = acceleration.add(-10 * tpf, 0);
    }
}
