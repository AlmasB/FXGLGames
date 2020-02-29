package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.geowars.component.*;
import com.almasb.fxglgames.geowars.component.enemy.BouncerComponent;
import com.almasb.fxglgames.geowars.component.enemy.RunnerComponent;
import com.almasb.fxglgames.geowars.component.enemy.SeekerComponent;
import com.almasb.fxglgames.geowars.component.enemy.WandererComponent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GeoWarsFactory implements EntityFactory {

    private final GeoWarsConfig config;

    public GeoWarsFactory() {
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
        return spawnPoints[FXGLMath.random(0, 3)];
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        Canvas canvas = new Canvas(getAppWidth(), getAppHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.138, 0.138, 0.375, 0.56));

        return entityBuilder()
                .type(GRID)
                .from(data)
                .view(canvas)
                .with(new GraphicsUpdateComponent(canvas.getGraphicsContext2D()))
                .with(new GridComponent(canvas.getGraphicsContext2D()))
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        var emitter = ParticleEmitters.newExplosionEmitter(1);

        var t = texture("player.png");
        //t.setEffect(new Bloom());

        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox(t)
                .collidable()
                .with(new KeepOnScreenComponent().bothAxes())
                .with(new PlayerComponent(config.getPlayerSpeed()))
                .with(new ExhaustParticleComponent(emitter))
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        return entityBuilder()
                .type(BULLET)
                .from(data)
                .viewWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), 800))
                .with(new BulletComponent())
                .with(new OffscreenCleanComponent())
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(100, config.getWandererMaxMoveSpeed());

        var t = texture(red ? "RedWanderer.png" : "Wanderer.png", 80, 80).brighter();

        return entityBuilder()
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(20, 20), BoundingShape.box(40, 40)))
                .view(t)
                .with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new WandererComponent(moveSpeed, t, texture("wanderer_overlay.png", 80, 80)))
                .build();
    }

    @Spawns("Seeker")
    public Entity spawnSeeker(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(150, config.getSeekerMaxMoveSpeed());

        // TODO: red ? "RedSeeker.png" : "Seeker.png"

        return entityBuilder()
                .type(SEEKER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Seeker.png", 60, 60).brighter())
                .with(new HealthComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new SeekerComponent(FXGL.<GeoWarsApp>getAppCast().getPlayer(), moveSpeed))
                .build();
    }

    @Spawns("Runner")
    public Entity spawnRunner(SpawnData data) {
        return entityBuilder()
                .type(RUNNER)
                .at(getRandomSpawnPoint())
                .viewWithBBox("Runner.png")
                .with(new HealthComponent(config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new RunnerComponent(config.getRunnerMoveSpeed()),
                        new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), config.getRunnerMoveSpeed(), FXGLMath.random(250, 500)))
                .build();
    }

    @Spawns("Bouncer")
    public Entity spawnBouncer(SpawnData data) {
        double y = FXGLMath.random(0, getAppHeight() - 40);

        return entityBuilder()
                .type(BOUNCER)
                .at(0, y)
                .viewWithBBox(texture("Bouncer.png", 254 * 0.25, 304 * 0.25))
                .with(new HealthComponent(config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new BouncerComponent(config.getBouncerMoveSpeed()))
                .build();
    }

    @Spawns("Explosion")
    public Entity spawnExplosion(SpawnData data) {
        play("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return entityBuilder()
                .at(data.getX() - 40, data.getY() - 40)
                .view(texture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(0.75)).play())
                .with(new ExplosionParticleComponent())
                .with(new ExpireCleanComponent(Duration.seconds(1.6)))
                .build();
    }

    @Spawns("Portal")
    public Entity spawnPortal(SpawnData data) {
        return entityBuilder()
                .type(PORTAL)
                .from(data)
                .viewWithBBox("Portal.png")
                .with(new CollidableComponent(true))
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }

    @Spawns("Crystal")
    public Entity spawnCrystal(SpawnData data) {
        return entityBuilder()
                .type(CRYSTAL)
                .from(data)
                .scale(0.65, 0.65)
                .viewWithBBox(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .with(new CollidableComponent(true))
                .with(new CrystalComponent(), new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }
}
