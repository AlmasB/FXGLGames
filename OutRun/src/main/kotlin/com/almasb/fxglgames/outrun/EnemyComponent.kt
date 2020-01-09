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

package com.almasb.fxglgames.outrun

import com.almasb.fxgl.entity.component.Component
import javafx.beans.property.SimpleDoubleProperty
import java.lang.Math.min

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EnemyComponent : Component() {

    private var speed = 0.0
    private var dy = 0.0

    val boost = SimpleDoubleProperty(100.0)

    override fun onUpdate(tpf: Double) {
        speed = tpf * 200

        dy += tpf / 4;

        if (entity.x < 80 || entity.rightX > 600 - 80) {
            if (dy > 0.017) {
                dy -= tpf
            }
        }

        entity.y -= dy

        boost.set(min(boost.get() + tpf * 5, 100.0))
    }

    fun getSpeed() = dy

    fun up() {
        entity.translateY(-speed)
    }

    fun down() {
        entity.translateY(speed)
    }

    fun left() {
        if (entity.x >= speed)
            entity.translateX(-speed)
    }

    fun right() {
        if (entity.rightX + speed <= 600)
            entity.translateX(speed)
    }

    fun boost() {
        if (boost.get() <= speed * 2)
            return

        boost.set(Math.max(boost.get() - speed / 2, 0.0))
        entity.y -= speed
    }

    fun reset() {
        dy = 0.0

//        val anim = fadeOutIn(entity.view, Duration.seconds(1.5))
//        anim.animatedValue.interpolator = Interpolators.BOUNCE.EASE_IN()
//        anim.startInPlayState()
    }
}