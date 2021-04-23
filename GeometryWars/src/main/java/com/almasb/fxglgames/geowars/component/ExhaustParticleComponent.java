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

package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.random;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExhaustParticleComponent extends ParticleComponent {

    private PlayerComponent playerComponent;

    public ExhaustParticleComponent(ParticleEmitter emitter) {
        super(emitter);
    }

    @Override
    public void onAdded() {
        var playerWidth = entity.getWidth() / 2.0;
        var playerHeight = entity.getHeight() / 2.0;

        var emitter = getEmitter();
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(2);
        emitter.setEmissionRate(1);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setStartColor(Color.BLUE);
        emitter.setEndColor(Color.BLUEVIOLET);
        //emitter.setAllowParticleRotation(true);
        emitter.setSize(8, 8);
        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(-0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 1.25)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);

        emitter.setSpawnPointFunction(i -> {
            return new Point2D(playerWidth - 8, playerHeight - 8);
        });
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return true;
    }
}