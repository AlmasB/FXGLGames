package com.almasb.fxglgames;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GameEntityFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .from(data)
                .view(new Rectangle(getAppWidth(), getAppHeight()))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder()
                .type(EntityType.PLAYER)
                .from(data)
                .viewWithBBox("player.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("asteroid")
    public Entity newAsteroid(SpawnData data) {
        return entityBuilder()
                .type(EntityType.ASTEROID)
                .from(data)
                .viewWithBBox("asteroid.png")
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 100))
                .collidable()
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Point2D dir = data.get("dir");

        return entityBuilder()
                .type(EntityType.BULLET)
                .from(data)
                .viewWithBBox("bullet.png")
                .with(new ProjectileComponent(dir, 500))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("explosion")
    public Entity newExplosion(SpawnData data) {
        //play("explosion.wav");

        var emitter = ParticleEmitters.newExplosionEmitter(350);
        emitter.setMaxEmissions(1);
        emitter.setSize(2, 10);
        emitter.setStartColor(Color.WHITE);
        emitter.setEndColor(Color.BLUE);
        emitter.setSpawnPointFunction(i -> new Point2D(64, 64));

        return entityBuilder()
                .from(data)
                .view(texture("explosion.png").toAnimatedTexture(16, Duration.seconds(0.66)).play())
                .with(new ExpireCleanComponent(Duration.seconds(0.66)))
                .with(new ParticleComponent(emitter))
                .build();
    }

    @Spawns("scoreText")
    public Entity newScoreText(SpawnData data) {
        String text = data.get("text");

        var e = entityBuilder()
                .from(data)
                .view(getUIFactory().newText(text, 24))
                .with(new ExpireCleanComponent(Duration.seconds(0.66)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(0.66))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() - 30))
                .buildAndPlay();

        return e;
    }
}
