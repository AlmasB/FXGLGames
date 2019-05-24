package com.almasb.fxglgames.geojumper

import com.almasb.fxgl.app.*
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.*
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.*
import com.almasb.fxgl.physics.box2d.dynamics.BodyType
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
        onBtnDown(MouseButton.PRIMARY, "Jump") {
            playerControl.jump()
        }

        getInput().addAction(object : UserAction("Rewind") {
            override fun onAction() {
                playerControl.rewind()
            }

            override fun onActionEnd() {
                playerControl.endRewind()
            }
        }, KeyCode.R)
    }

    override fun initGame() {
        getGameWorld().addEntityFactory(Factory())

        //gameWorld.addEntity(Entities.makeScreenBounds(40.0))

        // ground
        entityBuilder()
                .at(0.0, LEVEL_HEIGHT)
                .bbox(HitBox(BoundingShape.box(getAppWidth()*1.0, 40.0)))
                .with(PhysicsComponent())
                .buildAndAttach()

        initPlayer()
        initPlatforms()
    }

    override fun initPhysics() {
        getPhysicsWorld().setGravity(0.0, 1200.0)

        getPhysicsWorld().addCollisionHandler(object : CollisionHandler(EntityType.PLAYER, EntityType.PLATFORM) {
            override fun onCollisionBegin(player: Entity, platform: Entity) {

                // only if player actually lands on the platform
                if (player.bottomY <= platform.y) {

                    platform.getComponent(PlatformControl::class.java).stop()

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
        physics.addGroundSensor(HitBox(Point2D(10.0, 60.0), BoundingShape.box(10.0, 5.0)))

        playerControl = PlayerControl()

        val player = entityBuilder()
                .type(EntityType.PLAYER)
                .at(getAppWidth() / 2.0, LEVEL_HEIGHT - 60.0)
                .viewWithBBox(Rectangle(30.0, 60.0, Color.BLUE))
                .with(physics, CollidableComponent(true))
                .with(playerControl)
                .buildAndAttach()

        getGameScene().viewport.setBounds(0, 0, getAppWidth(), LEVEL_HEIGHT.toInt())
        getGameScene().viewport.bindToEntity(player, 0.0, getAppHeight() / 2.0)
    }

    private fun initPlatforms() {
        for (y in 0..LEVEL_HEIGHT.toInt() step 200) {

            entityBuilder()
                    .at(0.0, y * 1.0)
                    .view(getUIFactory().newText("$y", Color.BLACK, 16.0))
                    .buildAndAttach()

            spawn("platform", 20.0, y * 1.0)
        }
    }
}

class PlayerControl : Component() {

    private lateinit var physics: PhysicsComponent

    private val playerPoints = ArrayDeque<Point2D>()
    private val platformPoints = arrayListOf<Point2D>()

    private var isRewinding = false

    private var t = 0.0

    override fun onUpdate(tpf: Double) {

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
        entity.getComponent(PhysicsComponent::class.java).overwritePosition(point)

        if (platformPoints.isNotEmpty()) {
            platformPoints.forEach {
                spawn("platform", it).getComponent(PlatformControl::class.java).stop()
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

class PlatformControl : Component() {

    private val speed = FXGLMath.random(100.0, 400.0)

    private lateinit var physics: PhysicsComponent

    override fun onAdded() {
        physics.setOnPhysicsInitialized {
            physics.velocityX = speed
        }
    }

    override fun onUpdate(tpf: Double) {
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

        return entityBuilder()
                .type(EntityType.PLATFORM)
                .from(data)
                .viewWithBBox(Rectangle(100.0, 40.0))
                .with(physics, CollidableComponent(true))
                .with(PlatformControl())
                .build()
    }
}

enum class EntityType {
    PLAYER, PLATFORM
}

fun main(args: Array<String>) {
    GameApplication.launch(GeoJumperApp::class.java, args)
}