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

package com.almasb.geowars.grid;

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.MainViewComponent;
import javafx.geometry.Point2D;

import com.almasb.ents.AbstractControl;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;

import java.util.List;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AdditionalLineControl extends AbstractControl {

    private PointMass end11, end12, end21, end22;
    private Line line;
    private GraphicsContext g;

    public AdditionalLineControl(PointMass end11, PointMass end12,
                                 PointMass end21, PointMass end22) {
        this.end11 = end11;
        this.end12 = end12;
        this.end21 = end21;
        this.end22 = end22;
    }

    @Override
    public void onAdded(Entity entity) {
        entity.getComponent(MainViewComponent.class).ifPresent(viewComponent -> {
            List<Node> list = viewComponent.getView().getChildrenUnmodifiable();
            if (!list.isEmpty()) {
                line = (Line) list.get(0);
            }
        });

        g = entity.getComponentUnsafe(GraphicsComponent.class).getValue();
        //assert line != null;
    }

    private Point2D position1() {
        return end11.getPosition().midpoint(end12.getPosition());
    }

    private Point2D position2() {
        return end21.getPosition().midpoint(end22.getPosition());
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
//        line.setStartX(position1().getX());
//        line.setStartY(position1().getY());
//        line.setEndX(position2().getX());
//        line.setEndY(position2().getY());

        g.strokeLine(position1().getX(), position1().getY(),
                position2().getX(), position2().getY());
    }
}