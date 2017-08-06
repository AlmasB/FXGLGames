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

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.getd
import com.almasb.fxgl.app.set
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.entity.GameEntity
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.parser.text.TextLevelParser
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.settings.GameSettings
import com.almasb.fxgl.ui.ProgressBar
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
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
            version = "0.3"
            isProfilingEnabled = false
            isIntroEnabled = false
            isMenuEnabled = false
            applicationMode = ApplicationMode.DEVELOPER
        }
    }

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

    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars.put("time", 0.0)
    }

    override fun initGame() {
        val parser = TextLevelParser(gameWorld.getEntityFactory())
        val level = parser.parse("level0.txt")

        gameWorld.setLevel(level)

        val player = gameWorld.spawn("player", width / 2 - 20.0, level.height.toDouble())
        playerControl = player.getControl(PlayerControl::class.java)

        val bg = gameWorld.spawn("background") as GameEntity
        bg.positionComponent.yProperty().bind(gameScene.viewport.yProperty())

        gameScene.viewport.setBounds(0, 0, 600, level.height)
        gameScene.viewport.bindToEntity(player, width / 2.0, height - 80.0)
    }

    override fun initPhysics() {
        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.OBSTACLE) {
            override fun onCollisionBegin(player: Entity, wall: Entity) {
                // reset player to last checkpoint
                playerControl.reset()
                wall.getControl(ObstacleControl::class.java).hit()
            }
        })

        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.FINISH) {
            override fun onCollisionBegin(player: Entity, finish: Entity) {
                display.showMessageBox("Demo Over. Your Time: %.2f s\n".format(getd("time")) +
                        "Thanks for playing!", this@OutRunApp::exit)
            }
        })
    }

    private lateinit var ui: Text

    override fun initUI() {
        val label = uiFactory.newText("", 72.0)
        uiFactory.centerTextBind(label)

        val count = SimpleIntegerProperty(3)

        label.textProperty().bind(count.asString())

        gameScene.addUINode(label)

        val anim = uiFactory.fadeIn(label, Duration.seconds(1.0))
        anim.cycleCount = 3
        anim.animatedValue.interpolator = Interpolators.CIRCULAR.EASE_IN()
        anim.startInPlayState()

        val timerAction = masterTimer.runAtInterval(Runnable {
            count.set(count.get() - 1)
        }, Duration.seconds(1.0))

        count.addListener({ _, _, newValue ->
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
        boostBar.translateX = width - 200.0
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

    private var firstTime = true

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

        set("time", getd("time") + tpf)
    }
}

fun main(args: Array<String>) {
    Application.launch(OutRunApp::class.java, *args);
}