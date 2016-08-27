package com.almasb.flappy;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FlappyBirdApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("Flappy Bird Clone");
        settings.setVersion("0.1");
        settings.setShowFPS(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(true);
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
    protected void initAssets() {

    }

    private Rectangle wallView(double width, double height) {
        Rectangle wall = new Rectangle(width, height);
        wall.setArcWidth(25);
        wall.setArcHeight(25);
        wall.setStroke(Color.DARKBLUE);
        wall.setStrokeWidth(2);
        return wall;
    }

    @Override
    protected void initGame() {

        double diff = getHeight() / 2;

        for (int i = 0; i < 100; i++) {
            double distance = Math.random() * (getHeight() - diff);

            Entities.builder()
                    .at(1000 + i * 500, 0 - 25)
                    .viewFromNode(wallView(50, distance))
                    .buildAndAttach(getGameWorld());

            Entities.builder()
                    .at(1000 + i * 500, 0 + distance + diff + 25)
                    .viewFromNode(wallView(50, getHeight() - diff - distance))
                    .buildAndAttach(getGameWorld());
        }



        playerControl = new PlayerControl();

        GameEntity player = Entities.builder()
                .at(100, 100)
                .viewFromTexture("player.png")
                .with(playerControl)
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().setBounds(0, 0, 500 * 500, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, 500, getHeight() / 2);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {
    }

    public static void main(String[] args) {
        launch(args);
    }
}
