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
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.dsl.FXGL.Companion.centerTextBind
import com.almasb.fxgl.dsl.FXGL.Companion.loopBGM
import com.almasb.fxgl.dsl.components.Effect
import com.almasb.fxgl.dsl.components.EffectComponent
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.level.text.TextLevelLoader
import com.almasb.fxgl.ui.ProgressBar
import com.almasb.fxglgames.outrun.EntityType.*
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * An example racing game in FXGL.
 *
 * BGM music from https://freesound.org/people/FoolBoyMedia/sounds/244088/
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OutRunApp : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 600
            height = 800;
            title = "OutRun"
            version = "0.3.5"
        }
    }

    private lateinit var playerComponent: PlayerComponent

    override fun initInput() {
        onKey(KeyCode.W, "Boost") {
            playerComponent.boost()
        }

        onKey(KeyCode.A, "Move Left") {
            playerComponent.left()
        }

        onKey(KeyCode.D, "Move Right") {
            playerComponent.right()
        }
    }

    override fun initGameVars(vars: MutableMap<String, Any>) {
        vars["time"] = 0.0
    }

    override fun onPreInit() {
        loopBGM("bgm.mp3")
    }

    override fun initGame() {
        getGameWorld().addEntityFactory(OutRunFactory())

        val level = getAssetLoader().loadLevel("level0.txt", TextLevelLoader(40, 40, '0'))

        getGameWorld().setLevel(level)

        val player = spawn("player", getAppWidth() / 2 - 40.0, level.height.toDouble())
        playerComponent = player.getComponent(PlayerComponent::class.java)

        spawn("enemy", getAppWidth() / 2 + 40.0, level.height.toDouble())

        val bg = spawn("background")
        bg.yProperty().bind(getGameScene().viewport.yProperty())

        getGameScene().viewport.setBounds(0, 0, 600, level.height)
        getGameScene().viewport.bindToEntity(player, getAppWidth() / 2.0, getAppHeight() - 80.0)
    }

    override fun initPhysics() {
        onCollisionBegin(PLAYER, OBSTACLE) { _, wall ->
            // reset player to last checkpoint
            playerComponent.reset()
            wall.getComponent(ObstacleComponent::class.java).hit()
        }

        onCollisionBegin(ENEMY, OBSTACLE) { enemy, wall ->
            // reset enemy to last checkpoint
            enemy.getComponent(EnemyComponent::class.java).reset()
            wall.getComponent(ObstacleComponent::class.java).hit()
        }

        onCollisionBegin(PLAYER, POWERUP) { player, boost ->
            boost.removeFromWorld()

            player.getComponent(EffectComponent::class.java).startEffect(object : Effect(Duration.seconds(1.0)) {
                override fun onStart(entity: Entity) {
                    playerComponent.applyExtraBoost()
                }

                override fun onEnd(entity: Entity) {
                    playerComponent.removeExtraBoost()
                }
            })
        }

        onCollisionBegin(PLAYER, FINISH) { _, _ ->
            showMessage("Demo Over. Your Time: %.2f s\n".format(getd("time")) +
                    "Thanks for playing!", FXGL.getGameController()::exit)
        }

        onCollisionBegin(PLAYER, ENEMY) { player, enemy ->
            player.translateX(-30.0)
            enemy.translateX(30.0)
        }
    }

    private lateinit var ui: Text

    override fun initUI() {
        val label = getUIFactoryService().newText("", 72.0)
        centerTextBind(label)

        val count = SimpleIntegerProperty(3)

        label.textProperty().bind(count.asString())

        addUINode(label)

        animationBuilder()
                .duration(Duration.seconds(1.0))
                .repeat(3)
                .interpolator(Interpolators.CIRCULAR.EASE_IN())
                .fadeIn(label)
                .buildAndPlay()

        val timerAction = run({
            count.set(count.get() - 1)
        }, Duration.seconds(1.0))

        count.addListener { _, _, newValue ->
            if (newValue.toInt() == 0) {
                timerAction.expire()
                removeUINode(label)
            }
        }

        // BOOST

        val boostBar = ProgressBar(false);
        boostBar.setWidth(100.0)
        boostBar.setHeight(15.0)
        boostBar.setFill(Color.GREEN.brighter())
        boostBar.setTraceFill(Color.GREEN.brighter())
        boostBar.translateX = getAppWidth() - 200.0
        boostBar.translateY = 25.0
        boostBar.isLabelVisible = false
        boostBar.setMaxValue(100.0)
        boostBar.currentValueProperty().bind(playerComponent.boost)

        addUINode(boostBar)

        // UI text

        ui = getUIFactoryService().newText("", 16.0)
        ui.translateX = 50.0
        ui.translateY = 50.0

        addUINode(ui)
    }

    private var firstTime = true

    override fun onUpdate(tpf: Double) {
        if (firstTime) {
            val flow = getUIFactoryService().newTextFlow()
            flow.append("OutRun Demo\n", Color.WHITE)
                .append(KeyCode.W, Color.GREEN).append(" - Boost\n", Color.WHITE)
                .append(KeyCode.A, Color.GREEN).append(" - Move Left\n", Color.WHITE)
                .append(KeyCode.D, Color.GREEN).append(" - Move Right", Color.WHITE)

            val vbox = VBox(flow)
            vbox.translateX = 200.0
            vbox.alignment = Pos.CENTER

            getDisplay().showBox("", vbox, getUIFactoryService().newButton("OK"))

            firstTime = false
        } else {
            ui.text = "Speed: %.2f".format(playerComponent.getSpeed())
        }

        set("time", getd("time") + tpf)
    }
}

fun main(args: Array<String>) {
    GameApplication.launch(OutRunApp::class.java, args);
}