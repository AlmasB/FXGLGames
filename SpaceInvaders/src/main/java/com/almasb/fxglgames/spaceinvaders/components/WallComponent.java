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

package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WallComponent extends Component {

    private final int originalLives;
    private int lives;

    public WallComponent(int lives) {
        this.lives = lives;
        originalLives = lives;
    }

    public void onHit() {
        lives--;

        animationBuilder()
                .autoReverse(true)
                .repeat(2)
                .interpolator(Interpolators.CIRCULAR.EASE_IN())
                .duration(Duration.seconds(0.33))
                .scale(entity)
                .to(new Point2D(1.2, 1.2))
                .buildAndPlay();

        if (lives == 0) {
            entity.getComponent(CollidableComponent.class).setValue(false);

            animationBuilder()
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .duration(Duration.seconds(0.8))
                    .onFinished(entity::removeFromWorld)
                    .translate(entity)
                    .from(entity.getPosition())
                    .to(new Point2D(entity.getX(), getAppHeight() + 10))
                    .buildAndPlay();

        } else if (lives == originalLives / 2) {
            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(texture("wall2.png", 232 / 3, 104 / 3));
            play("brick_hit.wav");
        }
    }
}
