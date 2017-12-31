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

import com.almasb.fxgl.app.texture
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.component.CollidableComponent
import com.almasb.fxgl.entity.control.EffectControl
import com.almasb.fxgl.entity.view.EntityView
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
class OutRunFactory : TextEntityFactory {

    override fun emptyChar(): Char {
        return '0'
    }

    override fun blockHeight(): Int {
        return 40
    }

    override fun blockWidth(): Int {
        return 40
    }

    @Spawns("background")
    fun newBackground(data: SpawnData): Entity {
        val view = EntityView()
        view.addNode(Rectangle(600.0, 800.0, Color.color(0.0, 0.5, 0.0)))

        val road = Rectangle(440.0, 800.0, Color.color(0.25, 0.25, 0.25))
        road.translateX = 80.0
        view.addNode(road)

        return Entities.builder()
                .type(EntityType.BACKGROUND)
                .from(data)
                .viewFromNode(view)
                .renderLayer(RenderLayer.BACKGROUND)
                .build()
    }

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
        val playerTexture = texture("player.png")

        return Entities.builder()
                .type(EntityType.PLAYER)
                .from(data)
                .viewFromNodeWithBBox(playerTexture)
                .with(CollidableComponent(true))
                .with(PlayerControl(), EffectControl())
                .build()
    }

    @Spawns("enemy")
    fun newEnemy(data: SpawnData): Entity {
        val playerTexture = texture("player.png").multiplyColor(Color.rgb(11, 33, 11))

        return Entities.builder()
                .type(EntityType.ENEMY)
                .from(data)
                .viewFromNodeWithBBox(playerTexture)
                .with(CollidableComponent(true))
                .with(EnemyControl())
                .build()
    }

    @SpawnSymbol('b')
    fun newPowerup(data: SpawnData): Entity {
        return Entities.builder()
                .type(EntityType.POWERUP)
                .from(data)
                .viewFromTextureWithBBox("powerup_boost.png")
                .with(CollidableComponent(true))
                .build()
    }

    @SpawnSymbol('1')
    fun newObstacle(data: SpawnData): Entity {
        val textures = arrayOf(
                "cone_up.png".to("cone_down.png"),
                "barrel_blue_up.png".to("barrel_blue_down.png")
                )

        val index = Random().nextInt(textures.size)

        return Entities.builder()
                .type(EntityType.OBSTACLE)
                .from(data)
                .viewFromTextureWithBBox(textures[index].first)
                .with(CollidableComponent(true))
                .with(ObstacleControl(textures[index].first, textures[index].second))
                .build()
    }

    @SpawnSymbol('F')
    fun newFinishLine(data: SpawnData): Entity {
        return Entities.builder()
                .type(EntityType.FINISH)
                .from(data)
                .viewFromNodeWithBBox(Rectangle(600.0, 40.0))
                .with(CollidableComponent(true))
                .build()
    }
}