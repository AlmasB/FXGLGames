package com.almasb.fxglgames;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AsteroidsApp extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(500);
        settings.setHeight(750);
        settings.setTitle("Asteroids");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, () -> player.getComponent(PlayerComponent.class).rotateLeft());
        onKey(KeyCode.D, () -> player.getComponent(PlayerComponent.class).rotateRight());
        onKey(KeyCode.W, () -> player.getComponent(PlayerComponent.class).move());

        onKeyDown(KeyCode.F, "Shoot", () -> player.getComponent(PlayerComponent.class).shoot());
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());

        spawn("background");

        player = spawn("player", getAppWidth() / 2, getAppHeight() / 2);

        run(() -> {

            Entity e = getGameWorld().create("asteroid", new SpawnData(100, 100));

            spawnWithScale(e, Duration.seconds(0.75), Interpolators.BOUNCE.EASE_OUT());

        }, Duration.seconds(2));
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(EntityType.BULLET, EntityType.ASTEROID, (bullet, asteroid) -> {
            bullet.removeFromWorld();
            asteroid.removeFromWorld();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
