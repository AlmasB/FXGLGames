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

import com.almasb.ents.Entity
import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.entity.GameEntity
import com.almasb.fxgl.entity.RenderLayer
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.parser.TextLevelParser
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.settings.GameSettings
import com.almasb.fxgl.ui.ProgressBar
import com.almasb.fxgl.ui.UIFactory
import javafx.animation.FadeTransition
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * An example racing game in FXGL (using Kotlin).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OutRunApp : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 600
            height = 800;
            title = "OutRun"
            version = "0.2"
            isProfilingEnabled = false
            isIntroEnabled = false
            isMenuEnabled = false
            applicationMode = ApplicationMode.DEVELOPER
        }
    }

    override fun initAssets() { }

    private lateinit var playerControl: PlayerControl

    override fun initInput() {
        input.addAction(object : UserAction("Boost") {
            override fun onAction() {
                playerControl.boost()
            }
        }, KeyCode.W)

        input.addAction(object : UserAction("Move Left") {
            override fun onAction() {
                playerControl.left()
            }
        }, KeyCode.A)

        input.addAction(object : UserAction("Move Right") {
            override fun onAction() {
                playerControl.right()
            }
        }, KeyCode.D)
    }

    override fun initGame() {
        val parser = TextLevelParser()
        parser.emptyChar = '0'
        parser.addEntityProducer('1', { x, y -> EntityFactory.newObstacle(x*40.0, y*40.0) })
        parser.addEntityProducer('F', { x, y -> EntityFactory.newFinishLine(y) })

        val level = parser.parse("level0.txt")

        gameWorld.setLevel(level)

        val player = EntityFactory.newPlayer(width / 2 - 20, level.height*40.0)
        playerControl = player.getControlUnsafe(PlayerControl::class.java)
        gameWorld.addEntity(player)

        val bg = EntityFactory.newBackground()
        bg.positionComponent.yProperty().bind(gameScene.viewport.yProperty())
        bg.mainViewComponent.renderLayer = object : RenderLayer {

            override fun index(): Int {
                return 0
            }

            override fun name(): String {
                return "BACKGROUND"
            }
        }
        gameWorld.addEntity(bg)

        gameScene.viewport.setBounds(0, 0, 600, level.height*40)
        gameScene.viewport.bindToEntity(player, width / 2, height - 80)
    }

    override fun initPhysics() {
        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.OBSTACLE) {
            override fun onCollisionBegin(player: Entity, wall: Entity) {
                // reset player to last checkpoint
                playerControl.reset()
                wall.getControlUnsafe(ObstacleControl::class.java).hit()
            }
        })

        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.FINISH) {
            override fun onCollisionBegin(player: Entity, finish: Entity) {
                display.showConfirmationBox("Your Time: ${now / 1000000000.0} s\n" +
                        "Press YES to close the game", { yes -> exit() })
            }
        })
    }

    private lateinit var ui: Text

    override fun initUI() {
        val label = uiFactory.newText("", 72.0)
        label.translateX = width / 2
        label.translateY = height / 2

        val count = SimpleIntegerProperty(3)

        label.textProperty().bind(count.asString())

        gameScene.addUINode(label)

        val timerAction = masterTimer.runAtInterval( {
            count.set(count.get() - 1)
            val animation = FadeTransition(Duration.seconds(0.33), label)
            animation.fromValue = 0.0
            animation.toValue = 1.0
            animation.play()
        }, Duration.seconds(1.0))

        count.addListener({ o, old, newValue ->
            if (newValue.toInt() == 0) {
                timerAction.expire()
                gameScene.removeUINode(label)
            }
        })

        // BOOST

        val boostBar = ProgressBar(false);
        boostBar.setWidth(100.0)
        boostBar.setHeight(15.0)
        boostBar.setFill(Color.GREEN.brighter())
        boostBar.setTraceFill(Color.GREEN.brighter())
        boostBar.translateX = width - 200
        boostBar.translateY = 25.0
        boostBar.isLabelVisible = false
        boostBar.setMaxValue(100.0)
        boostBar.currentValueProperty().bind(playerControl.boost)

        gameScene.addUINode(boostBar)

        // UI text

        ui = uiFactory.newText("", 16.0)
        ui.translateX = 50.0
        ui.translateY = 50.0

        gameScene.addUINode(ui)
    }

    var firstTime = true

    override fun onUpdate(tpf: Double) {
        if (firstTime) {
            display.showMessageBox("OutRun Demo\n" +
                    "W - Boost\n" +
                    "A - Move Left\n" +
                    "D - Move Right")
            firstTime = false
        } else {
            ui.text = "Speed: %.2f".format(playerControl.getSpeed())
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(OutRunApp::class.java, *args);
}