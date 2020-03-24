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

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyComponent extends Component {

    // TODO: fix visibility hack
    protected LocalTimer attackTimer;
    protected Duration nextAttack = Duration.seconds(2);

    @Override
    public void onAdded() {
        attackTimer = FXGL.newLocalTimer();
        attackTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (attackTimer.elapsed(nextAttack)) {
            if (FXGLMath.randomBoolean(0.3f)) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }
    }

    protected void shoot() {
        spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

        play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    public void die() {
        spawn("Explosion", entity.getCenter());
        spawn("ParticleExplosion", entity.getCenter());

        entity.removeFromWorld();

        fire(new GameEvent(GameEvent.ENEMY_KILLED));
    }
}
