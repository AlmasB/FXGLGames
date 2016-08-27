package com.almasb.flappy;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FlappyBirdApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Flappy Bird Clone");
        settings.setVersion("0.1");
        settings.setShowFPS(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.SPACE);

        getInput().addAction(new UserAction("Exit") {
            @Override
            protected void onActionBegin() {
                pause();
                exit();
            }
        }, KeyCode.L);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        initBackground();
        initWalls();
        initPlayer();
    }

    private boolean requestNewGame = false;

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                requestNewGame = true;
            }
        });
    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    @Override
    protected void onPostUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            startNewGame();
        }
    }

    private void initBackground() {
        GameEntity bg = Entities.builder()
                .viewFromNode(new Rectangle(getWidth(), getHeight(), Color.WHITE))
                .with(new ColorChangingControl())
                .buildAndAttach(getGameWorld());

        bg.getPositionComponent().xProperty().bind(getGameScene().getViewport().xProperty());
        bg.getPositionComponent().yProperty().bind(getGameScene().getViewport().yProperty());
    }

    private Rectangle wallView(double width, double height) {
        Rectangle wall = new Rectangle(width, height);
        wall.setArcWidth(25);
        wall.setArcHeight(25);
        //wall.setStroke(Color.DARKBLUE);
        //wall.setStrokeWidth(2);
        return wall;
    }

    private void initWalls() {
        double distance = getHeight() / 2;

        for (int i = 0; i < 100; i++) {
            double topHeight = Math.random() * (getHeight() - distance);

            Entities.builder()
                    .at(1000 + i * 500, 0 - 25)
                    .type(EntityType.WALL)
                    .viewFromNodeWithBBox(wallView(50, topHeight))
                    .with(new CollidableComponent(true))
                    .with(new ColorChangingControl())
                    .buildAndAttach(getGameWorld());

            Entities.builder()
                    .at(1000 + i * 500, 0 + topHeight + distance + 25)
                    .type(EntityType.WALL)
                    .viewFromNodeWithBBox(wallView(50, getHeight() - distance - topHeight))
                    .with(new CollidableComponent(true))
                    .with(new ColorChangingControl())
                    .buildAndAttach(getGameWorld());
        }
    }

    private void initPlayer() {
        playerControl = new PlayerControl();

        Texture view = getAssetLoader().loadTexture("player.png")
                .toStaticAnimatedTexture(2, Duration.seconds(0.33));

        GameEntity player = Entities.builder()
                .at(100, 100)
                .type(EntityType.PLAYER)
                .bbox(new HitBox("BODY", BoundingShape.box(70, 60)))
                .viewFromNode(view)
                .with(new CollidableComponent(true))
                .with(playerControl)
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().setBounds(0, 0, 500 * 500, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, 500, getHeight() / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
