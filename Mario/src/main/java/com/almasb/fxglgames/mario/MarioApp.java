package com.almasb.fxglgames.mario;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.scene.Viewport;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(15 * 70);
        settings.setHeight(10 * 70);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PhysicsComponent.class).setVelocityX(0);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerControl.class).jump();
            }
        }, KeyCode.W);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MarioFactory());

        setLevelFromMap("tmx/mario.tmx");

        player = getGameWorld().spawn("player", 50, 50);

        Viewport viewport = getGameScene().getViewport();

        viewport.setBounds(-1500, 0, 1500, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        viewport.setLazy(true);

        getGameWorld().spawn("enemy", 470, 50);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.removeFromWorld();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                getDisplay().showMessageBox("Level Complete!", () -> {
                    System.out.println("Dialog closed!");
                });
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
