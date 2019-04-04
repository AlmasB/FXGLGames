package com.almasb.fxglgames.mario;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.scene.Viewport;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    private static final int MAX_LEVEL = 3;

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
            protected void onActionBegin() {
                player.getComponent(PlayerControl.class).jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Boxes") {
            @Override
            protected void onAction() {
                player.getBoundingBoxComponent().hitBoxesProperty().forEach(box -> {
                    System.out.println(box.getMinXWorld() + " " + box.getMaxXWorld());
                });
            }
        }, KeyCode.F);

//        getInput().addAction(new UserAction("Shrink") {
//            @Override
//            protected void onActionBegin() {
//                var platform = getGameWorld().getEntitiesByType(MarioType.PLATFORM).get(1);
//
//                System.out.println(platform);
//
//                platform.setScaleY(0.3);
//            }
//        }, KeyCode.G);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", 0);
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
        getPhysicsWorld().setGravity(0, 760);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.removeFromWorld();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                door.removeComponent(CollidableComponent.class);

                getGameScene().getViewport().fade(() -> {
                    nextLevel();
                });
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
