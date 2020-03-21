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

package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BrickComponent extends Component {

    private int lives = 2;

    private boolean canBeHit = true;

    private Color color;

    public BrickComponent(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void onHit() {
        if (!canBeHit)
            return;

        canBeHit = false;

        lives--;

        if (lives == 1) {
            playHitAnimation();

            entity.getViewComponent().clearChildren();

            var colorName = entity.getString("color");

            entity.getViewComponent().addChild(texture("brick_" + colorName + "_cracked.png"));
        } else if (lives == 0) {

            var colorName = entity.getString("color");
            var t = texture("brick_" + colorName + "_cracked.png");

            var textures = new ArrayList<Texture>();

            var t1 = t.subTexture(new Rectangle2D(0, 0, 32, 32));
            var t2 = t.subTexture(new Rectangle2D(32, 0, 32, 32));
            var t3 = t.subTexture(new Rectangle2D(32 + 32, 0, 32, 32));

            textures.add(t1);
            textures.add(t2);
            textures.add(t3);

            for (int i = 0; i < textures.size(); i++) {
                var te = textures.get(i);

                entityBuilder()
                        .at(entity.getPosition().add(i*32, 0))
                        .view(te)
                        .with(new ProjectileComponent(new Point2D(0, 1), random(550, 700)).allowRotation(false))
                        .with(new ExpireCleanComponent(Duration.seconds(0.7)).animateOpacity())
                        .buildAndAttach();
            }

            entity.removeFromWorld();
        }
    }

    private void playHitAnimation() {
        animationBuilder()
                .onFinished(() -> canBeHit = true)
                .repeat(4)
                .autoReverse(true)
                .duration(Duration.seconds(0.02))
                .interpolator(Interpolators.BACK.EASE_OUT())
                .translate(entity)
                .from(entity.getPosition())
                .to(entity.getPosition().add(FXGLMath.random(5, 10), 0))
                .buildAndPlay();
    }
}
