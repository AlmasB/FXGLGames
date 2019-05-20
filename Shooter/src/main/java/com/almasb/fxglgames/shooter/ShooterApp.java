package com.almasb.fxglgames.shooter;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShooterApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Shooter App");
        settings.setVersion("1.0");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                getGameWorld().spawn("Bullet", getInput().getMouseXWorld(), getAppHeight() - 10);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("enemies", 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ShooterFactory());

        getGameTimer().runAtInterval(() -> {

            int numEnemies = getGameState().getInt("enemies");

            if (numEnemies < 5) {
                getGameWorld().spawn("Enemy",
                        FXGLMath.random(0, getAppWidth() - 40),
                        FXGLMath.random(0, getAppHeight() / 2 - 40)
                );

                getGameState().increment("enemies", +1);
            }

        }, Duration.seconds(1));
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        physicsWorld.addCollisionHandler(new CollisionHandler(ShooterType.BULLET, ShooterType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                enemy.removeFromWorld();

                getGameState().increment("enemies", -1);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
