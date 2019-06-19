package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxglgames.geowars.component.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GeoWarsFactory implements EntityFactory {

    private final GeoWarsConfig config;

    public GeoWarsFactory() {
        //config = getAssetLoader().loadKV("config.kv").to(GeoWarsConfig.class);

        config = new GeoWarsConfig();
    }

    private static final int SPAWN_DISTANCE = 50;

    /**
     * These correspond to top-left, top-right, bottom-right, bottom-left.
     */
    private Point2D[] spawnPoints = new Point2D[] {
            new Point2D(SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE),
            new Point2D(SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE)
    };

    private Point2D getRandomSpawnPoint() {
        return spawnPoints[FXGLMath.random(3)];
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        // TODO: move this to proper PlayerControl
        OldPositionComponent oldPosition = new OldPositionComponent();
        oldPosition.valueProperty().addListener((obs, old, newPos) -> {
            oldPosition.getEntity().rotateToVector(newPos.subtract(old));
        });

        return entityBuilder()
                .type(GeoWarsType.PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox("Player.png")
                .with(new CollidableComponent(true), oldPosition)
                .with(new PlayerControl(config.getPlayerSpeed()), new KeepOnScreenComponent().bothAxes())
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        return entityBuilder()
                .type(GeoWarsType.BULLET)
                .from(data)
                .viewWithBBox("Bullet.png")
                .with(new BulletComponent(), new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), 800),
                        new BulletControl(FXGL.<GeoWarsApp>getAppCast().getGrid()),
                        new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(100, config.getWandererMaxMoveSpeed());

        return entityBuilder()
                .type(GeoWarsType.WANDERER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(red ? "RedWanderer.png" : "Wanderer.png")
                //.with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new WandererControl(moveSpeed))
                .build();
    }

    @Spawns("Seeker")
    public Entity spawnSeeker(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(150, config.getSeekerMaxMoveSpeed());

        return entityBuilder()
                .type(GeoWarsType.SEEKER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(red ? "RedSeeker.png" : "Seeker.png")
                //.with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new SeekerComponent(FXGL.<GeoWarsApp>getAppCast().getPlayer(), moveSpeed))
                .build();
    }

    @Spawns("Runner")
    public Entity spawnRunner(SpawnData data) {
        return entityBuilder()
                .type(GeoWarsType.RUNNER)
                .at(getRandomSpawnPoint())
                .viewWithBBox("Runner.png")
                //.with(new HealthComponent(config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new RunnerControl(config.getRunnerMoveSpeed()),
                        new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), config.getRunnerMoveSpeed(), FXGLMath.random(250, 500)))
                .build();
    }

    @Spawns("Bouncer")
    public Entity spawnBouncer(SpawnData data) {
        double y = FXGLMath.random(0, getAppHeight() - 40);

        Circle view = new Circle(20, Color.color(0.4, 0.7, 0.3, 0.3));
        view.setStrokeWidth(2.5);
        view.setStroke(Color.color(0.4, 0.7, 0.3, 0.8));

        return entityBuilder()
                .type(GeoWarsType.BOUNCER)
                .at(0, y)
                .viewWithBBox(view)
                //.with(new HealthComponent(config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new BouncerComponent(config.getBouncerMoveSpeed()))
                .build();
    }

    @Spawns("Explosion")
    public Entity spawnExplosion(SpawnData data) {
        play("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return entityBuilder()
                .with(new ExpireCleanComponent(Duration.seconds(1)))
                .build();
//        // explosion particle effect
//        ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(100);
//        emitter.setSize(6, 8);
//        emitter.setNumParticles(24);
//        emitter.setExpireFunction(i -> Duration.seconds(1.5));
//        emitter.setVelocityFunction(i -> Vec2.fromAngle(360 / 24 *i).toPoint2D().multiply(FXGLMath.random(45, 50)));
//        emitter.setBlendMode(BlendMode.SRC_OVER);
//        emitter.setStartColor(Color.WHITE);
//        emitter.setEndColor(Color.RED.interpolate(Color.YELLOW, 0.5));
//
//        com.almasb.fxgl.effect.ParticleControl control = new ParticleControl(emitter);
//
//        Entity explosion = entityBuilder()
//                .at(data.getX() - 5, data.getY() - 10)
//                .with(control)
//                .buildAndAttach();
//
//        control.setOnFinished(explosion::removeFromWorld);
//
//        return entityBuilder()
//                .at(data.getX() - 40, data.getY() - 40)
//                .view(texture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
//                .with(new ExpireCleanControl(Duration.seconds(1.8)))
//                .build();
    }

    @Spawns("Shockwave")
    public Entity spawnShockwave(SpawnData data) {
        Circle circle = new Circle(10, null);
        circle.setStroke(Color.DARKGOLDENROD);
        circle.setStrokeWidth(2);

        return entityBuilder()
                .from(data)
                .view(circle)
                .with(new ShockwaveControl())
                .build();
    }

    @Spawns("Portal")
    public Entity spawnPortal(SpawnData data) {
        return entityBuilder()
                .type(GeoWarsType.PORTAL)
                .from(data)
                .viewWithBBox("Portal.png")
                .with(new CollidableComponent(true))
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }

    @Spawns("Crystal")
    public Entity spawnCrystal(SpawnData data) {
        Entity crystal = entityBuilder()
                .type(GeoWarsType.CRYSTAL)
                .from(data)
                .viewWithBBox(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .with(new CollidableComponent(true))
                .with(new CrystalControl(), new ExpireCleanComponent(Duration.seconds(10)))
                .build();

        crystal.setScaleX(0.65);
        crystal.setScaleY(0.65);

        return crystal;
    }
}
