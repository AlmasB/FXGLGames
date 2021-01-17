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
import com.almasb.fxgl.core.math.Vec2;
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

    private double t = 0.0;
    private boolean up = true;

    public ExhaustParticleComponent(ParticleEmitter emitter) {
        super(emitter);
    }

    @Override
    public void onAdded() {
        var emitter = getEmitter();
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(2);
        emitter.setEmissionRate(1);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setStartColor(Color.BLUE);
        emitter.setEndColor(Color.BLUEVIOLET);
        emitter.setAllowParticleRotation(true);
        emitter.setSize(2, 8);
        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 1.25)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);

        emitter.setSpawnPointFunction(i -> {
            Vec2 offset = Vec2.fromAngle(playerComponent.getEntity().getRotation());

            return offset.mulLocal(-15).toPoint2D();
        });
        emitter.setVelocityFunction(i -> {
            var direction = Vec2.fromAngle(playerComponent.getEntity().getRotation());
            return direction.normalizeLocal().negateLocal().toPoint2D().multiply(10);
        });

        emitter.setControl(p -> {
            var x = p.position.x;
            var y = p.position.y;

            var noiseValue = FXGLMath.noise2D(x * 0.02 * t, y * 0.02 * t);
            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360.0;

            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 15));

            var vx = p.velocity.x * 0.9f + v.x * 0.1f;
            var vy = p.velocity.y * 0.9f + v.y * 0.1f;

            p.velocity.x = vx;
            p.velocity.y = vy;
        });
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        if (up) {
            t += tpf;
        } else {
            t -= tpf;
        }

        if (t > 7) {
            up = false;
        }

        if (t < 1) {
            up = true;
        }
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return true;
    }
}