package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Pixel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.geowars.component.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.Bloom;
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
