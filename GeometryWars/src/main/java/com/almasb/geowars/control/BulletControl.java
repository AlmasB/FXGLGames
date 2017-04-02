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

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.texture.Texture;
import com.almasb.geowars.grid.Grid;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BulletControl extends AbstractControl {

    static {
        ExhaustParticleControl.colorImage(Color.GOLD);
    }

    private BoundingBoxComponent bbox;

    private Point2D velocity;
    private Grid grid;

    public BulletControl(Grid grid) {
        this.grid = grid;
    }

    @Override
    public void onAdded(Entity entity) {
        velocity = entity.getControlUnsafe(ProjectileControl.class).getVelocity();
        bbox = entity.getComponentUnsafe(BoundingBoxComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        grid.applyExplosiveForce(velocity.magnitude() / 60 * 18, bbox.getCenterWorld(), 80 * 60 * tpf);

        if (bbox.getMinXWorld() < 0) {
            Entities.builder()
                    .at(0, bbox.getCenterWorld().getY())
                    .viewFromNode(new Texture(ExhaustParticleControl.coloredImages.get(Color.GOLD)))
                    .with(new ProjectileControl(new Point2D(1, FXGLMath.random(-1, 1)), 100), new ExpireCleanControl(Duration.seconds(1.2)))
                    .buildAndAttach(FXGL.getApp().getGameWorld());
        }
    }
}
