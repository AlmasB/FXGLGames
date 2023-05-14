package com.almasb.fxglgames.geowars.factory;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.IntervalSwitchComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import com.almasb.fxglgames.geowars.component.enemy.BouncerComponent;
import com.almasb.fxglgames.geowars.component.enemy.RunnerComponent;
import com.almasb.fxglgames.geowars.component.enemy.SeekerComponent;
import com.almasb.fxglgames.geowars.component.enemy.WandererComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.Config.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyFactory implements EntityFactory {

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

    @Spawns("Wanderer")
    public Entity spawnWanderer(SpawnData data) {
        var beepSwitch = new IntervalSwitchComponent(false, Duration.seconds(0.5));

        var e = entityBuilder(data)
                .type(WANDERER)
                .at(getRandomSpawnPoint())
                .bbox(new HitBox(new Point2D(15, 15), BoundingShape.box(30, 30)))
                .view(texture("Wanderer.png", 60, 60).toColor(Color.PURPLE).outline(Color.ALICEBLUE, 2))
                .with(beepSwitch)
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(new WandererComponent(WANDERER_MOVE_SPEED))
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
        var beepSwitch = new IntervalSwitchComponent(false, Duration.seconds(0.2));

        var e = entityBuilder()
                .type(SEEKER)
                .at(getRandomSpawnPoint())
                .viewWithBBox(texture("Seeker.png", 50, 50).toColor(Color.RED).outline(Color.ALICEBLUE, 2))
                .with(new HealthIntComponent(ENEMY_HP))
                .with(new CollidableComponent(true))
                .with(beepSwitch)
                .with(new AutoRotationComponent().withSmoothing())
                .with(new SeekerComponent(FXGL.<GeoWarsApp>getAppCast().getPlayer(), SEEKER_MOVE_SPEED))
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
                .viewWithBBox(texture("Runner.png", 258 * 0.2, 220 * 0.2).toColor(Color.GREEN).outline(Color.ALICEBLUE, 2))
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
}
