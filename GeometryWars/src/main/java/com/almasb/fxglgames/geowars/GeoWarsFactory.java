package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.geowars.component.*;
import com.almasb.fxglgames.geowars.component.enemy.BouncerComponent;
import com.almasb.fxglgames.geowars.component.enemy.RunnerComponent;
import com.almasb.fxglgames.geowars.component.enemy.SeekerComponent;
import com.almasb.fxglgames.geowars.component.enemy.WandererComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GeoWarsFactory implements EntityFactory {

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

    private final GeoWarsConfig config;

    public GeoWarsFactory() {
        config = new GeoWarsConfig();
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return entityBuilder(data)
                .type(GRID)
                .with(new GridComponent())
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        var texture = texture("Player.png");
        texture.setEffect(new Bloom(0.7));

        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox(texture)
                .collidable()
                .zIndex(1000)
                .with(new PlayerComponent(config.getPlayerSpeed()))
                //.with(new ExhaustParticleComponent(ParticleEmitters.newExplosionEmitter(1)))
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        var t = texture("Bullet.png");
        t.setScaleX(1.2);
        t.setScaleY(1.2);

        var expireClean = new ExpireCleanComponent(Duration.seconds(0.5)).animateOpacity();
        expireClean.pause();

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox("Bullet.png")
                .view(t)
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), 1200))
                .with(new BulletComponent())
                .with(expireClean)
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        boolean red = FXGLMath.randomBoolean((float)config.getRedEnemyChance());

        int moveSpeed = red ? config.getRedEnemyMoveSpeed()
                : FXGLMath.random(100, config.getWandererMaxMoveSpeed());

        var t = texture(red ? "RedWanderer.png" : "Wanderer.png", 80, 80).brighter();

        var name = "spark_04.png";

        var w = 128;
        var h = 128;

        var t2 = texture("particles/" + name, w, h).multiplyColor(Color.BLUE.brighter());
        t2.setTranslateX(-(w / 2.0 - 80 / 2.0));
        t2.setTranslateY(-(h / 2.0 - 80 / 2.0));
        //t2.setEffect(new BoxBlur(15, 15, 3));

        return entityBuilder()
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(20, 20), BoundingShape.box(40, 40)))
                //.view(t2)
                .view(t)
                .with(new HealthIntComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
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
                .with(new HealthIntComponent(red ? config.getRedEnemyHealth() : config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new SeekerComponent(FXGL.<GeoWarsApp>getAppCast().getPlayer(), moveSpeed))
                .build();
    }

    @Spawns("Runner")
    public Entity spawnRunner(SpawnData data) {
        return entityBuilder()
                .type(RUNNER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Runner.png", 258 * 0.25, 220 * 0.25))
                .with(new HealthIntComponent(config.getEnemyHealth()))
                .with(new CollidableComponent(true))
                .with(new RunnerComponent(config.getRunnerMoveSpeed()))
                .with(new AutoRotationComponent().withSmoothing())
                .build();
    }

    @Spawns("Bouncer")
    public Entity spawnBouncer(SpawnData data) {
        return entityBuilder()
                .type(BOUNCER)
                .at(0, random(0, getAppHeight() - 40))
                .viewWithBBox(texture("Bouncer.png", 254 * 0.25, 304 * 0.25))
                .with(new HealthIntComponent(config.getEnemyHealth()))
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
                .with(new ExpireCleanComponent(Duration.seconds(1.6)))
                .with(new ExplosionParticleComponent())
                .build();
    }

    @Spawns("Crystal")
    public Entity spawnCrystal(SpawnData data) {
        var name = "light_02.png";

        var w = 64;
        var h = 64;

        var t = texture("particles/" + name, w, h).multiplyColor(Color.YELLOW.brighter());
        t.setTranslateX(-(w / 2.0 - 32 / 2.0));
        t.setTranslateY(-(h / 2.0 - 32 / 2.0));
        t.setEffect(new BoxBlur(15, 15, 3));

        return entityBuilder(data)
                .type(CRYSTAL)
                .scale(0.65, 0.65)
                .view(t)
                .viewWithBBox(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .zIndex(100)
                .with(new CollidableComponent(true))
                .with(new CrystalComponent())
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }
}
