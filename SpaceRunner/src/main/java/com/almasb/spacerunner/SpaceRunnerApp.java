package com.almasb.spacerunner;

import com.almasb.ents.Entity;
import com.almasb.ents.component.UserDataComponent;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.spacerunner.control.PlayerControl;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Runner");
        settings.setVersion("0.1");
        settings.setWidth(500);
        settings.setHeight(500);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                playerControl.shoot();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getGameScene().addGameView(new ScrollingBackgroundView(getAssetLoader().loadTexture("bg_0.png"),
                Orientation.HORIZONTAL));

        SpaceRunnerFactory factory = FXGL.getInstance(SpaceRunnerFactory.class);

        GameEntity player = factory.newPlayer(50, getHeight() / 2);
        playerControl = player.getControlUnsafe(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, 50, getHeight() / 2);

        getGameWorld().addEntity(player);
        getGameWorld().addEntity(factory.newEnemy(500, 300));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                EntityType ownerType = (EntityType) bullet.getComponentUnsafe(UserDataComponent.class).getValue();

                if (!Entities.getType(enemy).isType(ownerType)) {
                    PositionComponent position = Entities.getPosition(enemy);
                    getGameWorld().addEntity(FXGL.getInstance(SpaceRunnerFactory.class).newEnemy(position.getX() + 500, 300));

                    bullet.removeFromWorld();
                    enemy.removeFromWorld();
                }
            }
        });
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
