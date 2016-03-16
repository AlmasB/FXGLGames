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
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.entity.GameEntity
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.parser.TextLevelParser
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import javafx.scene.input.KeyCode

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
            version = "0.1"
            isIntroEnabled = false
            isMenuEnabled = false
        }
    }

    override fun initAssets() { }

    private lateinit var playerControl: PlayerControl

    override fun initInput() {
//        input.addAction(object : UserAction("Move Up") {
//            override fun onAction() {
//                playerControl.up()
//            }
//        }, KeyCode.W)
//
//        input.addAction(object : UserAction("Move Down") {
//            override fun onAction() {
//                playerControl.down()
//            }
//        }, KeyCode.S)

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
        parser.addEntityProducer('1', { x, y -> EntityFactory.newBlock(x*40.0, y*40.0) })
        parser.addEntityProducer('F', { x, y -> EntityFactory.newFinishLine(y) })

        val level = parser.parse("level0.txt")

        gameWorld.setLevel(level)

        val player = EntityFactory.newPlayer(width / 2 - 20, level.height*40.0)
        playerControl = player.getControlUnsafe(PlayerControl::class.java)
        gameWorld.addEntity(player)

        gameScene.viewport.setBounds(0, 0, 600, level.height*40)
        gameScene.viewport.bindToEntity(player, width / 2, height - 80)
    }

    override fun initPhysics() {
        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.OBSTACLE) {
            override fun onCollisionBegin(player: Entity, wall: Entity) {
                // reset player to last checkpoint
                playerControl.reset()
            }
        })

        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.FINISH) {
            override fun onCollisionBegin(player: Entity, finish: Entity) {
                display.showMessageBox("Your Time: ${now / 1000000000.0} s")
            }
        })
    }

    override fun initUI() { }

    override fun onUpdate(tpf: Double) { }
}

fun main(args: Array<String>) {
    Application.launch(OutRunApp::class.java, *args);
}