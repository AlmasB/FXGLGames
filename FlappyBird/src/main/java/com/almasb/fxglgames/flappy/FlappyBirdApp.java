package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FlappyBirdApp extends GameApplication {

    private PlayerComponent playerComponent;
    private boolean requestNewGame = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Flappy Bird Clone");
        settings.setVersion("0.3");
        settings.setFontUI("sf_atarian_system.ttf");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerComponent.jump();
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("stageColor", Color.BLACK);
        vars.put("score", 0);
    }

    private boolean loopBGM = true;

    @Override
    protected void initGame() {
        if (loopBGM) {
            loopBGM("bgm.mp3");
            loopBGM = false;
        }

        initBackground();
        initPlayer();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                requestNewGame();
            }
        });
    }

    @Override
    protected void initUI() {
        Text uiScore = getUIFactory().newText("", 72);
        uiScore.setTranslateX(getAppWidth() - 200);
        uiScore.setTranslateY(50);
        uiScore.fillProperty().bind(getGameState().objectProperty("stageColor"));
        uiScore.textProperty().bind(getGameState().intProperty("score").asString());

        getGameScene().addUINode(uiScore);
    }

    @Override
    protected void onUpdate(double tpf) {
        getGameState().increment("score", +1);

        if (getGameState().getInt("score") == 3000) {
            showGameOver();
        }

        if (requestNewGame) {
            requestNewGame = false;
            getGameController().startNewGame();
        }
    }

    private void initBackground() {
        Rectangle rect = new Rectangle(getAppWidth(), getAppHeight(), Color.WHITE);

        Entity bg = entityBuilder()
                .type(EntityType.BACKGROUND)
                .view(rect)
                .with("rect", rect)
                .with(new ColorChangingComponent())
                .buildAndAttach();

        bg.xProperty().bind(getGameScene().getViewport().xProperty());
        bg.yProperty().bind(getGameScene().getViewport().yProperty());
    }

    private void initPlayer() {
        playerComponent = new PlayerComponent();

        Entity player = entityBuilder()
                .at(100, 100)
                .type(EntityType.PLAYER)
                .bbox(new HitBox("BODY", BoundingShape.box(70, 60)))
                .view(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5)).loop())
                .with(new CollidableComponent(true))
                .with(playerComponent, new WallBuildingComponent())
                .build();

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 3, getAppHeight() / 2);

        playSpawnAnimation(player);
    }

    private void playSpawnAnimation(Entity player) {
        player.setScaleX(0);
        player.setScaleY(0);

        getGameWorld().addEntity(player);

        animationBuilder()
                .duration(Duration.seconds(0.86))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .scale(player)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();
    }

    public void requestNewGame() {
        requestNewGame = true;
    }

    private void showGameOver() {
        getDisplay().showMessageBox("Demo Over. Thanks for playing!", getGameController()::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
