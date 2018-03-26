package com.almasb.fxglgames.geojumper

import com.almasb.fxgl.app.*
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.component.CollidableComponent
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.*
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.*

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

        input.addAction(object : UserAction("Rewind") {
            override fun onAction() {
                playerControl.rewind()
            }

            override fun onActionEnd() {
                playerControl.endRewind()
            }
        }, KeyCode.R)
    }

    override fun initGame() {
        gameWorld.setEntityFactory(Factory())

        //gameWorld.addEntity(Entities.makeScreenBounds(40.0))

        // ground
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

                // only if player actually lands on the platform
                if (player.bottomY <= platform.y) {

                    platform.getControl(PlatformControl::class.java).stop()

                    playerControl.startNewCapture()
                }
            }

            override fun onCollisionEnd(player: Entity, platform: Entity) {

                // only if player is jumping off that platform
                if (player.bottomY <= platform.y) {
                    playerControl.removedPlatformAt(platform.position)
                    platform.removeFromWorld()
                }
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

            Entities.builder()
                    .at(0.0, y * 1.0)
                    .viewFromNode(uiFactory.newText("$y", Color.BLACK, 16.0))
                    .buildAndAttach()

            spawn("platform", 20.0, y * 1.0)
        }
    }
}

class PlayerControl : Control() {

    private lateinit var physics: PhysicsComponent

    private val playerPoints = ArrayDeque<Point2D>()
    private val platformPoints = arrayListOf<Point2D>()

    private var isRewinding = false

    private var t = 0.0

    override fun onUpdate(entity: Entity, tpf: Double) {

        physics.velocityX = 0.0

        t += tpf
        if (t >= 0.05 && !isRewinding) {
            playerPoints.addLast(entity.position)

            t = 0.0
        }
    }

    fun startNewCapture() {
        //if (isOnPlatform()) {
            playerPoints.clear()
            platformPoints.clear()
        //}
    }

    fun removedPlatformAt(point: Point2D) {
        platformPoints.add(point)
    }

    fun rewind() {
        if (playerPoints.isEmpty()) {
            isRewinding = false
            entity.getComponent(CollidableComponent::class.java).value = true
            t = 0.0
            return
        }

        //println("print")

        isRewinding = true
        entity.getComponent(CollidableComponent::class.java).value = false

        val point = playerPoints.removeLast()
        entity.getControl(PhysicsControl::class.java).reposition(point)

        if (platformPoints.isNotEmpty()) {
            platformPoints.forEach {
                spawn("platform", it).getControl(PlatformControl::class.java).stop()
            }

            platformPoints.clear()
        }
    }

    fun endRewind() {
        isRewinding = false
        entity.getComponent(CollidableComponent::class.java).value = true
        t = 0.0
    }

    fun isOnPlatform(): Boolean = physics.isOnGround || FXGLMath.abs(physics.velocityY) < 2

    fun jump() {
        if (isOnPlatform()) {
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

class Factory : EntityFactory {

    @Spawns("platform")
    fun newPlatform(data: SpawnData): Entity {
        val physics = PhysicsComponent()
        physics.setBodyType(BodyType.KINEMATIC)

        return Entities.builder()
                .type(EntityType.PLATFORM)
                .from(data)
                .viewFromNodeWithBBox(Rectangle(100.0, 40.0))
                .with(physics, CollidableComponent(true))
                .with(PlatformControl())
                .build()
    }
}

enum class EntityType {
    PLAYER, PLATFORM
}

fun main(args: Array<String>) {
    Application.launch(GeoJumperApp::class.java, *args);
}