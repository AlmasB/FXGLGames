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

package com.almasb.outrun

import com.almasb.ents.AbstractControl
import com.almasb.ents.Entity
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.MainViewComponent
import com.almasb.fxgl.entity.component.PositionComponent
import javafx.animation.FadeTransition
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PlayerControl : AbstractControl() {

    private lateinit var position: PositionComponent

    private var speed = 0.0
    private var dy = 0.0

    val boost = SimpleDoubleProperty(100.0)

    override fun onAdded(entity: Entity) {
        position = entity.getComponentUnsafe(PositionComponent::class.java)
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        speed = tpf * 200

        dy += tpf / 4;

        position.y -= dy

        boost.set(Math.min(boost.get() + tpf * 5, 100.0))
    }

    fun up() {
        position.translateY(-speed)
    }

    fun down() {
        position.translateY(speed)
    }

    fun left() {
        if (position.x >= speed)
            position.translateX(-speed)
    }

    fun right() {
        if (position.x + getEntity().getComponentUnsafe(BoundingBoxComponent::class.java).width + speed <= 600)
            position.translateX(speed)
    }

    fun boost() {
        if (boost.get() <= speed * 2)
            return

        boost.set(Math.max(boost.get() - speed / 2, 0.0))
        position.y -= speed
    }

    fun reset() {
        dy = 0.0
        val view = getEntity().getComponentUnsafe(MainViewComponent::class.java).view

        val fade = FadeTransition(Duration.seconds(1.0), view)
        with(fade) {
            fromValue = 1.0
            toValue = 0.0
            cycleCount = 2
            isAutoReverse = true
        }

        fade.play()
    }
}