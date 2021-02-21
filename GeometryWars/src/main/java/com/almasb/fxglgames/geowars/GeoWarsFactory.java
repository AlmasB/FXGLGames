package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.geowars.component.*;
import com.almasb.fxglgames.geowars.component.enemy.*;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.Config.*;
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

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return entityBuilder(data)
                .type(GRID)
                //.with(new BackgroundStarsComponent())
                .with(new GridComponent())
                .build();
    }

    @Spawns("ParticleLayer")
    public Entity spawnParticleLayer(SpawnData data) {
        return entityBuilder(data)
                .type(PARTICLE_LAYER)
                .with(new ParticleCanvasComponent())
                .zIndex(5000)
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        var texture = texture("player.png");
        texture.setEffect(new Bloom(0.7));

        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2, getAppHeight() / 2)
                .viewWithBBox(texture)
                .collidable()
                .zIndex(1000)
                .with(new PlayerComponent(PLAYER_SPEED))
                //.with(new ExhaustParticleComponent(ParticleEmitters.newExplosionEmitter(1)))
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        var expireClean = new ExpireCleanComponent(Duration.seconds(0.5)).animateOpacity();
        expireClean.pause();

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), BULLET_MOVE_SPEED))
                .with(new BulletComponent())
                .with(expireClean)
                .build();
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        int moveSpeed = random(WANDERER_MIN_MOVE_SPEED, WANDERER_MAX_MOVE_SPEED);

        var t = texture("Wanderer.png", 80, 80).brighter();

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
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new WandererComponent(moveSpeed, t, texture("wanderer_overlay.png", 80, 80)))
                .build();
    }

    @Spawns("Seeker")
    public Entity spawnSeeker(SpawnData data) {

        int moveSpeed = random(SEEKER_MIN_MOVE_SPEED, SEEKER_MAX_MOVE_SPEED);

        return entityBuilder()
                .type(SEEKER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Seeker.png", 60, 60).brighter())
                .with(new HealthIntComponent(ENEMY_HP))
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
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new RunnerComponent(RUNNER_MOVE_SPEED))
                .with(new AutoRotationComponent().withSmoothing())
                .build();
    }

    @Spawns("Bomber")
    public Entity spawnBomber(SpawnData data) {
        return entityBuilder(data)
                .type(BOMBER)
                .viewWithBBox(texture("Bomber.png", 202 * 0.15, 166 * 0.15))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(1, 0), BOMBER_MOVE_SPEED))
                .with(new BomberComponent())
                .build();
    }

    @Spawns("Bouncer")
    public Entity spawnBouncer(SpawnData data) {
        return entityBuilder()
                .type(BOUNCER)
                .at(0, random(0, getAppHeight() - 40))
                .viewWithBBox(texture("Bouncer.png", 254 * 0.25, 304 * 0.25))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new BouncerComponent(BOUNCER_MOVE_SPEED))
                .build();
    }

    @Spawns("Explosion")
    public Entity spawnExplosion(SpawnData data) {
        int numParticles = data.hasKey("numParticles") ? data.get("numParticles") : 200;

        play("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return entityBuilder()
                .at(data.getX() - 40, data.getY() - 40)
                .type(EXPLOSION)
                .view(texture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(0.75)).play())
                .with(new ExplosionParticleComponent(numParticles))
                .build();
    }

    @Spawns("Shockwave")
    public Entity spawnShockwave(SpawnData data) {
        var view = new Rectangle(40, 40, null);
        view.setStrokeWidth(2);
        view.setStroke(Color.GOLD);
        view.setCache(true);
        view.setCacheHint(CacheHint.SCALE);

        var e = entityBuilder()
                .at(data.getX() - 40, data.getY() - 40)
                .type(SHOCKWAVE)
                .viewWithBBox(view)
                .collidable()
                .build();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(e)
                .from(new Point2D(1, 1))
                .to(new Point2D(15, 15))
                .buildAndPlay();

        animationBuilder()
                .onFinished(() -> e.removeFromWorld())
                .fadeOut(e)
                .buildAndPlay();

        return e;
    }

    @Spawns("ShockwavePickup")
    public Entity spawnShockwavePickup(SpawnData data) {
        var view = new Rectangle(15, 15, null);
        view.setStrokeWidth(2);
        view.setStroke(Color.GOLD);

        return entityBuilder(data)
                .type(SHOCKWAVE_PICKUP)
                .viewWithBBox(view)
                .collidable()
                .zIndex(100)
                .with(new LiftComponent().yAxisDistanceDuration(15, Duration.seconds(1)))
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
                .scale(0.25, 0.25)
                .view(t)
                .viewWithBBox(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .zIndex(100)
                .with(new CollidableComponent(true))
                .with(new CrystalComponent())
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .build();
    }
}
