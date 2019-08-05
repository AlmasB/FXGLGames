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

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private ViewComponent view;

    private Texture texture;

    @Override
    public void onAdded() {
        getEntity().getTransformComponent().setRotationOrigin(new Point2D(42, 42));

        texture = FXGL.getAssetLoader().loadTexture("player.png");
        view.addChild(texture);
    }

    private double speed = 0;
    private int frames = 0;

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * 60;

        int frame = frames / 10;
        if (frame >= 8) {
            frame = 0;
            frames = 0;
        }

        texture.setViewport(new Rectangle2D(frame * 84, 0, 84, 84));
    }

    public void up() {
        getEntity().setRotation(270);
        getEntity().translateY(-5 * speed);
        frames++;
    }

    public void down() {
        getEntity().setRotation(90);
        getEntity().translateY(5 * speed);
        frames++;
    }

    public void left() {
        getEntity().setRotation(180);
        getEntity().translateX(-5 * speed);
        frames++;
    }

    public void right() {
        getEntity().setRotation(0);
        getEntity().translateX(5 * speed);
        frames++;
    }

    public void shoot() {
        FXGL.spawn("Bullet", new SpawnData(getEntity().getCenter()).put("direction", angleToVector()));
    }

    private Point2D angleToVector() {
        double angle = getEntity().getRotation();
        if (angle == 0) {
            return new Point2D(1, 0);
        } else if (angle == 90) {
            return new Point2D(0, 1);
        } else if (angle == 180) {
            return new Point2D(-1, 0);
        } else {    // 270
            return new Point2D(0, -1);
        }
    }
}
