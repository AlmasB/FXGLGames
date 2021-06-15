package com.almasb.fxglgames.cc;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Camera3D;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.multiplayer.MultiplayerService;
import com.almasb.fxgl.net.Connection;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.cc.EntityType.*;

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalApp extends GameApplication {

    private Camera3D camera;
    private Entity player1;
    private Entity player2;

    private Input clientInput;

    private boolean isServer;

    private Connection<Bundle> connection;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.set3D(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.addEngineService(MultiplayerService.class);
    }

    @Override
    protected void initInput() {
        var speed = 0.25;

        onKey(KeyCode.W, () -> player1.translateZ(speed));
        onKey(KeyCode.S, () -> player1.translateZ(-speed));
        onKey(KeyCode.A, () -> player1.translateX(-speed));
        onKey(KeyCode.D, () -> player1.translateX(speed));

        clientInput = new Input();

        onKeyBuilder(clientInput, KeyCode.W)
                .onAction(() -> player2.translateZ(speed));
        onKeyBuilder(clientInput, KeyCode.S)
                .onAction(() -> player2.translateZ(-speed));
        onKeyBuilder(clientInput, KeyCode.A)
                .onAction(() -> player2.translateX(-speed));
        onKeyBuilder(clientInput, KeyCode.D)
                .onAction(() -> player2.translateX(speed));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        runOnce(() -> {
            getDialogService().showConfirmationBox("Are you the host?", yes -> {
                isServer = yes;

                camera = getGameScene().getCamera3D();

                getGameWorld().addEntityFactory(new CrystalGameFactory());

                if (yes) {
                    var server = getNetService().newTCPServer(55555);
                    server.setOnConnected(conn -> {
                        connection = conn;

                        getExecutor().startAsyncFX(() -> onServer());
                    });

                    server.startAsync();

                } else {
                    var client = getNetService().newTCPClient("localhost", 55555);
                    client.setOnConnected(conn -> {
                        connection = conn;

                        getExecutor().startAsyncFX(() -> onClient());
                    });

                    client.connectAsync();
                }
            });
        }, Duration.seconds(0.5));
    }

    private void onServer() {
        camera.getTransform().translateY(-10);
        camera.getTransform().translateX(5);

        var ground = spawn("ground", 0, 5, 0);
        getService(MultiplayerService.class).spawn(connection, ground, "ground");

        camera.getTransform().lookAt(ground.getPosition3D());

        player1 = spawn("platform", 0, 1, 0);
        getService(MultiplayerService.class).spawn(connection, player1, "platform");

        player2 = spawn("platform", 0, 0, 0);
        getService(MultiplayerService.class).spawn(connection, player2, "platform");

        run(() -> {
            var point = FXGLMath.randomPoint(new Rectangle2D(-5, -5, 10, 10));

            var c = spawn("crystal", point.getX(), -5, point.getY());
            getService(MultiplayerService.class).spawn(connection, c, "crystal");
        }, Duration.seconds(2));

        getService(MultiplayerService.class).addInputReplicationReceiver(connection, clientInput);

        initCollisions();
    }

    private void initCollisions() {
        onCollisionBegin(CRYSTAL, GROUND, (crystal, g) -> {
            crystal.removeFromWorld();

            inc("score", -1);
        });

        onCollisionBegin(CRYSTAL, PLATFORM, (crystal, platform) -> {
            crystal.removeFromWorld();

            inc("score", +1);
        });
    }

    private void onClient() {
        player1 = new Entity();

        camera.getTransform().translateY(-10);
        camera.getTransform().translateX(-5);
        camera.getTransform().lookAt(new Point3D(0, 5, 0));

        getService(MultiplayerService.class).addEntityReplicationReceiver(connection, getGameWorld());
        getService(MultiplayerService.class).addInputReplicationSender(connection, getInput());
    }

    @Override
    protected void initUI() {
        var scoreText = getUIFactoryService().newText("", Color.DARKGRAY, 14.0);
        scoreText.textProperty().bind(getip("score").asString("Score: %d"));

        addUINode(scoreText, 50, 50);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (isServer) {
            clientInput.update(tpf);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}



//            var vector = platform.getPosition3D().subtract(new Point3D(5, -10, -15));
//
//            var distance = vector.magnitude() * 0.8;
//
//            vector = vector.normalize().multiply(distance);
//
//            animationBuilder()
//                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
//                    .duration(Duration.seconds(0.4))
//                    .repeat(2)
//                    .autoReverse(true)
//                    .animate(new AnimatedPoint3D(
//                            new Point3D(5, -10, -15),
//                            new Point3D(5, -10, -15).add(vector)
//                    ))
//                    .onProgress(p -> {
//                        camera.getTransform().setPosition3D(p);
//                    })
//                    .buildAndPlay();
