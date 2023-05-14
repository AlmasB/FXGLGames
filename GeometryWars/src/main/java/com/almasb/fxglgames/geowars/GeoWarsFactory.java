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
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Pixel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.geowars.component.*;
import com.almasb.fxglgames.geowars.component.enemy.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.stream.Collectors;

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
    private static final Point2D[] spawnPoints = new Point2D[] {
            new Point2D(SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, SPAWN_DISTANCE),
            new Point2D(getAppWidth() - SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE),
            new Point2D(SPAWN_DISTANCE, getAppHeight() - SPAWN_DISTANCE)
    };

    private static Point2D getRandomSpawnPoint() {
        return spawnPoints[FXGLMath.random(0, 3)];
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return entityBuilder(data)
                .type(GRID)
                .with(IS_BACKGROUND ? new BackgroundStarsComponent() : new CollidableComponent(false))
                .with(new GridComponent())
                .zIndex(BACKGROUND_Z_INDEX)
                .build();
    }

    @Spawns("ParticleLayer")
    public Entity spawnParticleLayer(SpawnData data) {
        return entityBuilder(data)
                .type(PARTICLE_LAYER)
                .with(new ParticleCanvasComponent())
                .zIndex(PARTICLES_Z_INDEX)
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData data) {
        var texture = texture("PlayerNew.png").outline(Color.web("blue", 0.5), 5);
        if (!FXGL.isMobile()) {
            texture.setEffect(new Bloom(0.7));
        }

        return entityBuilder()
                .type(PLAYER)
                .at(getAppWidth() / 2.0 - texture.getWidth() / 2, getAppHeight() / 2.0 - texture.getHeight() / 2)
                .viewWithBBox(texture)
                .collidable()
                .zIndex(1000)
                .with(new PlayerComponent(PLAYER_SPEED))
                .with(new EffectComponent())
                .with(new ExhaustParticleComponent(ParticleEmitters.newExplosionEmitter(1)))
                .with(new KeepInBoundsComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())))
                .zIndex(PLAYER_Z_INDEX)
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        // bullet texture is 54x13, hence 6.5

        var expireClean = new ExpireCleanComponent(Duration.seconds(0.5)).animateOpacity();
        expireClean.pause();

        var t = ImagesKt.fromPixels(54, 13,
                texture("Bullet.png")
                        .pixels()
                        .stream()
                        .map(p -> {
                            // texture is 54 in X axis
                            double alphaMod = p.getX() / 54.0;

                            return new Pixel(p.getX(), p.getY(), Color.color(p.getR(), p.getG(), p.getB(), p.getA() * alphaMod), p.getParent());
                        })
                        .collect(Collectors.toList())
        );

        var e = entityBuilder(data)
                .at(data.getX(), data.getY() - 6.5)
                .type(BULLET)
                .viewWithBBox(new Texture(t))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(data.get("direction"), BULLET_MOVE_SPEED))
                .with(new BulletComponent())
                .with(expireClean)
                .zIndex(BULLET_Z_INDEX)
                .rotationOrigin(0, 6.5)
                .build();

        // creating entities can be expensive on mobile, so pool bullets
        e.setReusable(true);

        return e;
    }

    // this allows to "reset" the bullet after it is returned from the pool
    public static void respawnBullet(Entity entity, SpawnData data) {
        play("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        entity.setPosition(data.getX(), data.getY() - 6.5);
        entity.setOpacity(1);
        entity.setVisible(true);

        entity.removeComponent(RicochetComponent.class);
        entity.removeComponent(ExpireCleanComponent.class);

        var expireClean = new ExpireCleanComponent(Duration.seconds(0.5)).animateOpacity();
        expireClean.pause();

        entity.addComponent(expireClean);

        Point2D dir = data.get("direction");

        entity.getComponent(ProjectileComponent.class).setDirection(dir);
    }

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        int moveSpeed = random(WANDERER_MIN_MOVE_SPEED, WANDERER_MAX_MOVE_SPEED);

        var beepSwitch = new IntervalSwitchComponent(false, Duration.seconds(0.5));

        var e = entityBuilder(data)
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(15, 15), BoundingShape.box(30, 30)))
                .view(texture("Wanderer.png", 60, 60).toColor(Color.PURPLE).outline(Color.ALICEBLUE, 2))
                .with(beepSwitch)
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new WandererComponent(moveSpeed))
                .zIndex(ENEMIES_Z_INDEX)
                .build();

        e.setReusable(true);

        var overlay = texture("Wanderer_overlay.png", 60, 60).toColor(Color.FIREBRICK).outline(Color.PURPLE, 2);
        overlay.visibleProperty().bind(beepSwitch.valueProperty());

        e.getViewComponent().addChild(overlay);

        return e;
    }

    public static void respawnWanderer(Entity entity) {
        entity.getComponent(HealthIntComponent.class).setValue(ENEMY_HP);
        entity.setPosition(getRandomSpawnPoint());
    }

    @Spawns("Seeker")
    public Entity spawnSeeker(SpawnData data) {

        int moveSpeed = random(SEEKER_MIN_MOVE_SPEED, SEEKER_MAX_MOVE_SPEED);

        var beepSwitch = new IntervalSwitchComponent(false, Duration.seconds(0.2));

        var e = entityBuilder()
                .type(SEEKER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Seeker.png", 50, 50).toColor(Color.RED).outline(Color.ALICEBLUE, 2))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(beepSwitch)
                .with(new AutoRotationComponent().withSmoothing())
                .with(new SeekerComponent(FXGL.<GeoWarsApp>getAppCast().getPlayer(), moveSpeed))
                .zIndex(ENEMIES_Z_INDEX)
                .build();

        var overlay = texture("Seeker_overlay.png", 50, 50).toColor(Color.BLACK).outline(Color.WHITESMOKE, 2);
        overlay.visibleProperty().bind(beepSwitch.valueProperty());

        e.getViewComponent().addChild(overlay);

        return e;
    }

    @Spawns("Runner")
    public Entity spawnRunner(SpawnData data) {
        return entityBuilder()
                .type(RUNNER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Runner.png", 258 * 0.2, 220 * 0.2))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new RunnerComponent(RUNNER_MOVE_SPEED))
                .with(new AutoRotationComponent().withSmoothing())
                .zIndex(ENEMIES_Z_INDEX)
                .build();
    }

    @Spawns("Bomber")
    public Entity spawnBomber(SpawnData data) {
        return entityBuilder(data)
                .type(BOMBER)
                .viewWithBBox(texture("Bomber.png", 202 * 0.15, 166 * 0.15))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .neverUpdated()
                .zIndex(ENEMIES_Z_INDEX)
                .build();
    }

    @Spawns("Mine")
    public Entity spawnMine(SpawnData data) {
        var beepSwitch = new IntervalSwitchComponent(false, Duration.seconds(0.5));

        var e = entityBuilder(data)
                .type(MINE)
                .viewWithBBox(texture("mine.png", 315 * 0.2, 315 * 0.2))
                .with(beepSwitch)
                .with(new MineComponent())
                .with(new CollidableComponent(false))
                .zIndex(ENEMIES_Z_INDEX)
                .build();

        var overlay = texture("mine_red.png", 315 * 0.2, 315 * 0.2);
        overlay.visibleProperty().bind(beepSwitch.valueProperty());

        e.getViewComponent().addChild(overlay);

        // enable collidable after a while
        runOnce(() -> {
            if (e.isActive()) {
                e.getComponent(CollidableComponent.class).setValue(true);
            }
        }, Duration.seconds(1.35));

        return e;
    }

    @Spawns("Bouncer")
    public Entity spawnBouncer(SpawnData data) {
        return entityBuilder()
                .type(BOUNCER)
                .at(0, random(0, getAppHeight() - 40))
                .viewWithBBox(texture("Bouncer.png", 254 * 0.2, 304 * 0.2))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new BouncerComponent(BOUNCER_MOVE_SPEED))
                .zIndex(ENEMIES_Z_INDEX)
                .build();
    }

    @Spawns("Explosion")
    public Entity spawnExplosion(SpawnData data) {
        var e = entityBuilder(data)
                .at(data.getX() - 40, data.getY() - 40)
                .type(EXPLOSION)
                .with(new ExplosionParticleComponent())
                .build();

        e.setReusable(true);

        return e;
    }

    public static void respawnExplosion(Entity entity, SpawnData data) {
        entity.setPosition(data.getX() - 40, data.getY() - 40);

        int numParticles = data.hasKey("numParticles") ? data.get("numParticles") : 200;

        play("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        entity.getComponent(ExplosionParticleComponent.class).setNumParticles(numParticles);
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
}
