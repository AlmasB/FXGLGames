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

import com.almasb.fxgl.dsl.FXGL.Companion.texture
import com.almasb.fxgl.dsl.components.EffectComponent
import com.almasb.fxgl.dsl.entityBuilder
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns
import com.almasb.fxglgames.outrun.EntityType.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OutRunFactory : EntityFactory {

    @Spawns("background")
    fun newBackground(data: SpawnData): Entity {
        return entityBuilder(data)
                .type(BACKGROUND)
                .view(Rectangle(600.0, 800.0, Color.color(0.0, 0.5, 0.0)))
                .view(Rectangle(440.0, 800.0, Color.color(0.25, 0.25, 0.25)).also { it.translateX = 80.0 })
                .zIndex(-5)
                .build()
    }

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
        return entityBuilder(data)
                .type(PLAYER)
                .viewWithBBox("player.png")
                .collidable()
                .with(PlayerComponent(), EffectComponent())
                .build()
    }

    @Spawns("enemy")
    fun newEnemy(data: SpawnData): Entity {
        return entityBuilder(data)
                .type(ENEMY)
                .viewWithBBox(texture("player.png").multiplyColor(Color.rgb(11, 33, 11)))
                .collidable()
                .with(EnemyComponent())
                .build()
    }

    @Spawns("b")
    fun newPowerup(data: SpawnData): Entity {
        return entityBuilder(data)
                .type(POWERUP)
                .viewWithBBox("powerup_boost.png")
                .collidable()
                .build()
    }

    @Spawns("1")
    fun newObstacle(data: SpawnData): Entity {
        val textures = arrayOf(
                "cone_up.png" to "cone_down.png",
                "barrel_blue_up.png" to "barrel_blue_down.png"
        )

        val index = Random().nextInt(textures.size)

        return entityBuilder(data)
                .type(OBSTACLE)
                .viewWithBBox(textures[index].first)
                .collidable()
                .with(ObstacleComponent(textures[index].first, textures[index].second))
                .build()
    }

    @Spawns("F")
    fun newFinishLine(data: SpawnData): Entity {
        return entityBuilder(data)
                .type(FINISH)
                .viewWithBBox(Rectangle(600.0, 40.0))
                .collidable()
                .build()
    }
}