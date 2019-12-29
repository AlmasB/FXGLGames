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

package com.almasb.fxglgames.spacerunner.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;


/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyComponent extends Component {

    private LocalTimer attackTimer;
    private Duration nextAttack = Duration.seconds(2);

    @Override
    public void onAdded() {
        attackTimer = newLocalTimer();
        attackTimer.capture();
    }

    private double t = FXGLMath.random(1, 10000);

    @Override
    public void onUpdate(double tpf) {
        t += tpf;

        if (attackTimer.elapsed(nextAttack)) {
            if (FXGLMath.randomBoolean(0.8f)) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * FXGLMath.random(0.0, 1.0));
            attackTimer.capture();
        }

        //position.translateX(tpf * FXGL.<GameConfig>getGameConfig().getPlayerSpeed() + tpf * 60 * (FXGLMath.noise1D(t * 9.8) - 0.5));
        //position.translateY((FXGLMath.noise1D(t) - 0.5) * tpf * FXGLMath.random(150, 350));
    }

    private void shoot() {
        spawn("EnemyBullet", entity.getPosition().subtract(15, 0));
    }

    public void die() {
        spawn("Explosion", entity.getCenter());

        entity.removeFromWorld();
    }
}

