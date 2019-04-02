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

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.FXGL.Companion.texture
import com.almasb.fxgl.dsl.components.EffectComponent
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.components.CollidableComponent
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
        val view = EntityView()
        view.addNode(Rectangle(600.0, 800.0, Color.color(0.0, 0.5, 0.0)))

        val road = Rectangle(440.0, 800.0, Color.color(0.25, 0.25, 0.25))
        road.translateX = 80.0
        view.addNode(road)

        return FXGL.entityBuilder()
                .type(EntityType.BACKGROUND)
                .from(data)
                .view(view)
                .zIndex(-5)
                .build()
    }

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
        val playerTexture = texture("player.png")

        return FXGL.entityBuilder()
                .type(EntityType.PLAYER)
                .from(data)
                .viewWithBBox(playerTexture)
                .with(CollidableComponent(true))
                .with(PlayerComponent(), EffectComponent())
                .build()
    }

    @Spawns("enemy")
    fun newEnemy(data: SpawnData): Entity {
        val playerTexture = texture("player.png").multiplyColor(Color.rgb(11, 33, 11))

        return FXGL.entityBuilder()
                .type(EntityType.ENEMY)
                .from(data)
                .viewWithBBox(playerTexture)
                .with(CollidableComponent(true))
                .with(EnemyComponent())
                .build()
    }

    @Spawns("b")
    fun newPowerup(data: SpawnData): Entity {
        return FXGL.entityBuilder()
                .type(EntityType.POWERUP)
                .from(data)
                .viewWithBBox("powerup_boost.png")
                .with(CollidableComponent(true))
                .build()
    }

    @Spawns("1")
    fun newObstacle(data: SpawnData): Entity {
        val textures = arrayOf(
                "cone_up.png".to("cone_down.png"),
                "barrel_blue_up.png".to("barrel_blue_down.png")
                )

        val index = Random().nextInt(textures.size)

        return FXGL.entityBuilder()
                .type(EntityType.OBSTACLE)
                .from(data)
                .viewWithBBox(textures[index].first)
                .with(CollidableComponent(true))
                .with(ObstacleComponent(textures[index].first, textures[index].second))
                .build()
    }

    @Spawns("F")
    fun newFinishLine(data: SpawnData): Entity {
        return FXGL.entityBuilder()
                .type(EntityType.FINISH)
                .from(data)
                .viewWithBBox(Rectangle(600.0, 40.0))
                .with(CollidableComponent(true))
                .build()
    }
}