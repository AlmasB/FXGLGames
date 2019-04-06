package com.almasb.fxglgames.mario;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.GameView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.scene.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.mario.MarioType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    private static final int MAX_LEVEL = 3;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", getSettings().getApplicationMode() == ApplicationMode.RELEASE ? 0 : -1);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MarioFactory());

        player = null;
        nextLevel();

        // player must be spawned after call to nextLevel, otherwise player gets removed
        // before the update tick _actually_ adds the player to game world
        player = getGameWorld().spawn("player", 50, 50);

        Viewport viewport = getGameScene().getViewport();

        viewport.setBounds(-1500, 0, 1500, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        viewport.setLazy(true);
    }

    @Override
    protected void initPhysics() {
        // this seems to fix a rare javafx with delayed audio playback - https://bugs.openjdk.java.net/browse/JDK-8125515
//        var mediaPlayer = new MediaPlayer(new Media(getClass().getResource("/assets/sounds/jump.wav").toExternalForm()));
//        mediaPlayer.setVolume(0);
//        mediaPlayer.seek(Duration.ZERO);
//        mediaPlayer.play();

        getPhysicsWorld().setGravity(0, 760);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.removeFromWorld();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                door.removeComponent(CollidableComponent.class);

                getGameScene().getViewport().fade(() -> {
                    nextLevel();
                });
            }
        });

        onCollisionBegin(PLAYER, EXIT_SIGN, (player, sign) -> {
            sign.removeComponent(CollidableComponent.class);

            var texture = texture("exit_sign.png").brighter();
            texture.setTranslateX(sign.getX() + 9);
            texture.setTranslateY(sign.getY() + 13);

            var gameView = new GameView(texture, 150);

            getGameScene().addGameView(gameView);

            runOnce(() -> getGameScene().removeGameView(gameView), Duration.seconds(1.6));
        });

        onCollisionBegin(PLAYER, KEY_PROMPT, (player, prompt) -> {
            String key = prompt.getString("key");

            var entity = getGameWorld().create("keyCode", new SpawnData(prompt.getX(), prompt.getY()).put("key", key));
            entity.getTransformComponent().setScaleOrigin(new Point2D(15, 15));

            animationBuilder()
                    .interpolator(Interpolators.ELASTIC.EASE_OUT())
                    .scale(entity)
                    .from(new Point2D(0, 0))
                    .to(new Point2D(1, 1))
                    .buildAndPlay();

            getGameWorld().addEntity(entity);

            runOnce(() -> {
                animationBuilder()
                        .interpolator(Interpolators.ELASTIC.EASE_IN())
                        .onFinished(() -> getGameWorld().removeEntity(entity))
                        .scale(entity)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(0, 0))
                        .buildAndPlay();

            }, Duration.seconds(2.5));
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BUTTON) {
            @Override
            protected void onCollisionBegin(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                if (!keyEntity.isActive()) {
                    getGameWorld().addEntity(keyEntity);
                }

                // TODO: setter / getter for opacity?
                keyEntity.getViewComponent().opacityProperty().setValue(1);
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                keyEntity.getViewComponent().opacityProperty().setValue(0);
            }
        });
    }

    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            getDisplay().showMessageBox("You finished the game!");
            return;
        }

        if (player != null) {
            player.getComponent(PhysicsComponent.class).reposition(new Point2D(50, 50));
            player.setZ(Integer.MAX_VALUE);
        }

        inc("level", +1);

        setLevelFromMap("tmx/level" + geti("level")  + ".tmx");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
