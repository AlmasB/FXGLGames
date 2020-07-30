/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.pacman.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PaletteChangingComponent extends Component {

    private Texture texture;

    public PaletteChangingComponent(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    private double lastX = 0;
    private double lastY = 0;

    private double timeToSwitch = 0;
    private int spriteColor = 0;

    @Override
    public void onUpdate(double tpf) {
        timeToSwitch += tpf;

        if (timeToSwitch >= 5.0) {
            spriteColor = 160 * FXGLMath.random(0, 2);
            timeToSwitch = 0;
        }

        double dx = entity.getX() - lastX;
        double dy = entity.getY() - lastY;

        lastX = entity.getX();
        lastY = entity.getY();

        if (dx == 0 && dy == 0) {
            // didn't move
            return;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            // move was horizontal
            if (dx > 0) {
                texture.setViewport(new Rectangle2D(130*3, spriteColor, 130, 160));
            } else {
                texture.setViewport(new Rectangle2D(130*2, spriteColor, 130, 160));
            }
        } else {
            // move was vertical
            if (dy > 0) {
                texture.setViewport(new Rectangle2D(0, spriteColor, 130, 160));
            } else {
                texture.setViewport(new Rectangle2D(130, spriteColor, 130, 160));
            }
        }
    }
}
