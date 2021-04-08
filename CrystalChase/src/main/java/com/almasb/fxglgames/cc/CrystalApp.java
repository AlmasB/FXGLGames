package com.almasb.fxglgames.cc;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.cc.EntityType.*;

/**
 *.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalApp extends GameApplication {

    private Entity platform;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setExperimental3D(true);
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        var speed = 0.25;

        onKey(KeyCode.W, () -> platform.translateZ(speed));
        onKey(KeyCode.S, () -> platform.translateZ(-speed));
        onKey(KeyCode.A, () -> platform.translateX(-speed));
        onKey(KeyCode.D, () -> platform.translateX(speed));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        var camera = getGameScene().getCamera3D();
        camera.getTransform().translateY(-10);
        camera.getTransform().translateX(5);

        getGameWorld().addEntityFactory(new CrystalGameFactory());

        var ground = spawn("ground", 0, 5, 0);

        camera.getTransform().lookAt(ground.getPosition3D());

        platform = spawn("platform", 0, 1, 0);

        run(() -> {
            spawn("crystal", 0, -5, 0);
        }, Duration.seconds(2));
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(CRYSTAL, GROUND, (crystal, ground) -> {
            crystal.removeFromWorld();

            inc("score", -1);
        });

        onCollisionBegin(CRYSTAL, PLATFORM, (crystal, platform) -> {
            crystal.removeFromWorld();

            inc("score", +1);
        });
    }

    @Override
    protected void initUI() {
        var scoreText = getUIFactoryService().newText("", Color.DARKGRAY, 14.0);
        scoreText.textProperty().bind(getip("score").asString("Score: %d"));

        addUINode(scoreText, 50, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
