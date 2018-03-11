package com.almasb.fxglgames.geojumper

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.onBtnDown
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.CollidableComponent
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.physics.PhysicsComponent
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GeoJumperApp : GameApplication() {

    private val LEVEL_HEIGHT = 2600.0

    private lateinit var playerControl: PlayerControl

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 600
            height = 800
            title = "Geo Jumper"
            version = "0.1"
        }
    }

    override fun initInput() {
        onBtnDown(MouseButton.PRIMARY, "Jump", Runnable {
            playerControl.jump()
        })
    }

    override fun initGame() {
        //gameWorld.addEntity(Entities.makeScreenBounds(40.0))

        Entities.builder()
                .at(0.0, LEVEL_HEIGHT)
                .bbox(HitBox(BoundingShape.box(width*1.0, 40.0)))
                .with(PhysicsComponent())
                .buildAndAttach()

        initPlayer()
        initPlatforms()
    }

    override fun initPhysics() {
        physicsWorld.setGravity(0.0, 1200.0)

        physicsWorld.addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.PLATFORM) {
            override fun onCollisionBegin(player: Entity, platform: Entity) {
                platform.getControl(PlatformControl::class.java).stop()
            }

            override fun onCollisionEnd(player: Entity, platform: Entity) {
                platform.removeFromWorld()
            }
        })
    }

    private fun initPlayer() {
        val physics = PhysicsComponent()
        physics.setBodyType(BodyType.DYNAMIC)
        physics.isGenerateGroundSensor = true

        playerControl = PlayerControl()

        val player = Entities.builder()
                .type(EntityType.PLAYER)
                .at(width / 2.0, LEVEL_HEIGHT - 60.0)
                .viewFromNodeWithBBox(Rectangle(30.0, 60.0, Color.BLUE))
                .with(physics, CollidableComponent(true))
                .with(playerControl)
                .buildAndAttach()

        gameScene.viewport.setBounds(0, 0, width, LEVEL_HEIGHT.toInt())
        gameScene.viewport.bindToEntity(player, 0.0, height / 2.0)
    }

    private fun initPlatforms() {
        for (y in 0..LEVEL_HEIGHT.toInt() step 200) {
            val physics = PhysicsComponent()
            physics.setBodyType(BodyType.KINEMATIC)

            Entities.builder()
                    .type(EntityType.PLATFORM)
                    .at(20.0, y * 1.0)
                    .viewFromNodeWithBBox(Rectangle(100.0, 40.0))
                    .with(physics, CollidableComponent(true))
                    .with(PlatformControl())
                    .buildAndAttach()
        }
    }
}

class PlayerControl : Control() {

    private lateinit var physics: PhysicsComponent

    override fun onUpdate(entity: Entity, tpf: Double) {

    }

    fun jump() {
        if (physics.isOnGround || FXGLMath.abs(physics.velocityY) < 2) {
            physics.velocityY = -800.0
        }
    }
}

class PlatformControl : Control() {

    private val speed = FXGLMath.random(100.0, 400.0)

    private lateinit var physics: PhysicsComponent

    override fun onAdded(entity: Entity) {
        physics.setOnPhysicsInitialized {
            physics.velocityX = speed
        }
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        if (entity.rightX >= FXGL.getAppWidth()) {
            physics.velocityX = -speed
        }

        if (entity.x <= 0) {
            physics.velocityX = speed
        }
    }

    fun stop() {
        pause()
        physics.velocityX = 0.0
    }
}

enum class EntityType {
    PLAYER, PLATFORM
}

fun main(args: Array<String>) {
    Application.launch(GeoJumperApp::class.java, *args);
}