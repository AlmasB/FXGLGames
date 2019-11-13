package com.almasb.fxglgames;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Map;

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
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("lives", 3);
    }

    @Override
    protected void initGame() {
        getSettings().setGlobalSoundVolume(0.1);

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

            spawn("scoreText", new SpawnData(asteroid.getX(), asteroid.getY()).put("text", "+100"));

            killAsteroid(asteroid);
            bullet.removeFromWorld();

            inc("score", +100);
        });

        onCollisionBegin(EntityType.PLAYER, EntityType.ASTEROID, (player, asteroid) -> {
            killAsteroid(asteroid);

            player.setPosition(getAppWidth() / 2, getAppHeight() / 2);

            inc("lives", -1);
        });
    }

    private void killAsteroid(Entity asteroid) {
        Point2D explosionSpawnPoint = asteroid.getCenter().subtract(64, 64);

        spawn("explosion", explosionSpawnPoint);

        asteroid.removeFromWorld();
    }

    @Override
    protected void initUI() {
        var text = getUIFactory().newText("", 24);
        text.textProperty().bind(getip("score").asString("Score: [%d]"));

        getGameState().addListener("score", (prev, now) -> {
            animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(text)
                    .from(new Point2D(1, 1))
                    .to(new Point2D(1.2, 1.2))
                    .buildAndPlay();
        });

        addUINode(text, 20, 50);
        addVarText(20, 70, "lives");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
