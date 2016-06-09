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

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.ServiceType
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.EntityView
import com.almasb.fxgl.entity.GameEntity
import com.almasb.fxgl.entity.RenderLayer
import com.almasb.fxgl.entity.component.CollidableComponent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityFactory {

    //private lateinit var config: Config

    companion object {
        fun newBackground(): GameEntity {
            val view = EntityView()
//            view.renderLayer = object : RenderLayer {
//
//                override fun index(): Int {
//                    return 0
//                }
//
//                override fun name(): String {
//                    return "BACKGROUND"
//                }
//            }

            view.addNode(Rectangle(600.0, 800.0, Color.color(0.0, 0.5, 0.0)))

            val road = Rectangle(440.0, 800.0, Color.color(0.25, 0.25, 0.25))
            road.translateX = 80.0
            view.addNode(road)

            return Entities.builder()
                    .type(EntityType.BACKGROUND)
                    .at(0.0, 0.0)
                    .viewFromNode(view)
                    .build()
        }

        fun newPlayer(x: Double, y: Double): GameEntity {
            return Entities.builder()
                    .type(EntityType.PLAYER)
                    .at(x, y)
                    .viewFromTextureWithBBox("player.png")
                    .with(CollidableComponent(true))
                    .with(PlayerControl())
                    .build()
        }

        fun newObstacle(x: Double, y: Double): GameEntity {
            val textures = arrayOf(
                    "cone_up.png".to("cone_down.png"),
                    "barrel_blue_up.png".to("barrel_blue_down.png")
                    )

            val index = Random().nextInt(textures.size)

            return Entities.builder()
                    .type(EntityType.OBSTACLE)
                    .at(x, y)
                    .viewFromTextureWithBBox(textures[index].first)
                    .with(CollidableComponent(true))
                    .with(ObstacleControl(textures[index].first, textures[index].second))
                    .build()
        }

        fun newFinishLine(y: Int): GameEntity {
            return Entities.builder()
                    .type(EntityType.FINISH)
                    .at(0.0, y * 40.0)
                    //.viewFromTextureWithBBox("wall.png")
                    .viewFromNodeWithBBox(Rectangle(600.0, 40.0))
                    .with(CollidableComponent(true))
                    .build()
        }
    }
}