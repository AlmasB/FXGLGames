/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.MenuItem;
import com.almasb.fxgl.ui.UI;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.EnumSet;
import java.util.Map;

/**
 * A simple multiplayer pong.
 * Sounds from https://freesound.org/people/NoiseCollector/sounds/4391/ under CC BY 3.0.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication {

    private PongFactory factory;

    private Entity ball;
    private Entity bat1;
    private Entity bat2;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("0.3.2");
        settings.setFontUI("pong.ttf");
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.ONLINE));
    }

    @Override
    protected void preInit() {
        FXGL.getNet().addDataParser(ServerMessage.class, message -> {
            Platform.runLater(() -> {
                if (ball != null) {

                    ball.setPosition(new Point2D(message.ballPosition.x, message.ballPosition.y));
                    bat1.setY(message.bat1PositionY);
                    bat2.setY(message.bat2PositionY);
                }
            });
        });

        FXGL.getNet().addDataParser(ClientMessage.class, message -> {
            Platform.runLater(() -> {
                if (bat2 != null) {
                    if (message.up) {
                        bat2.getComponent(BatComponent.class).up();
                    } else if (message.down) {
                        bat2.getComponent(BatComponent.class).down();
                    } else {
                        bat2.getComponent(BatComponent.class).stop();
                    }
                }
            });
        });

        addExitListener(() -> {
            getNet().getConnection().ifPresent(conn -> conn.close());
        });
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Up", KeyCode.W));
        input.addInputMapping(new InputMapping("Down", KeyCode.S));
    }

    private GameMode mode;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("player1score", 0);
        vars.put("player2score", 0);
    }

    @Override
    protected void initGame() {
        if (getNet().getConnection().isPresent()) {
            mode = getNet().getConnection().get() instanceof Server ? GameMode.MP_HOST : GameMode.MP_CLIENT;
        } else {
            mode = GameMode.SP;

            getGameState().<Integer>addListener("player1score", (old, newScore) -> {
                if (newScore == 11) {
                    showGameOver("Player 1");
                }
            });

            getGameState().<Integer>addListener("player2score", (old, newScore) -> {
                if (newScore == 11) {
                    showGameOver("Player 2");
                }
            });
        }

        factory = new PongFactory(mode);

        initBackground();
        initScreenBounds();
        initBall();
        initPlayerBat();
        initEnemyBat();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("LEFT")) {
                    getGameState().increment("player2score", +1);
                } else if (boxB.getName().equals("RIGHT")) {
                    getGameState().increment("player1score", +1);
                }

                getAudioPlayer().playSound("hit_wall.wav");
                getGameScene().getViewport().shake(5);
            }
        });

        CollisionHandler ballBatHandler = new CollisionHandler(EntityType.BALL, EntityType.PLAYER_BAT) {
            @Override
            protected void onCollisionBegin(Entity a, Entity bat) {
                getAudioPlayer().playSound("hit_bat.wav");
                playHitAnimation(bat);
            }
        };

        getPhysicsWorld().addCollisionHandler(ballBatHandler);
        getPhysicsWorld().addCollisionHandler(ballBatHandler.copyFor(EntityType.BALL, EntityType.ENEMY_BAT));
    }

    @Override
    protected void initUI() {
        AppController controller = new AppController();
        UI ui = getAssetLoader().loadUI("main.fxml", controller);

        controller.getLabelScorePlayer().textProperty().bind(getGameState().intProperty("player1score").asString());
        controller.getLabelScoreEnemy().textProperty().bind(getGameState().intProperty("player2score").asString());

        getGameScene().addUI(ui);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (mode == GameMode.MP_HOST) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ServerMessage(new Vec2((float)ball.getX(), (float)ball.getY()), bat1.getY(), bat2.getY()));
            });
        }
    }

    private void initBackground() {
        getGameScene().setBackgroundColor(Color.rgb(0, 0, 5));
    }

    private void initScreenBounds() {
        Entity walls = Entities.makeScreenBounds(150);
        walls.setType(EntityType.WALL);
        walls.addComponent(new CollidableComponent(true));

        getGameWorld().addEntity(walls);
    }

    private void initBall() {
        ball = factory.newBall(getWidth() / 2 - 5, getHeight() / 2 - 5);

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setStartColor(Color.LIGHTYELLOW);
        emitter.setEndColor(Color.RED);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(5, 10);
        emitter.setEmissionRate(1);

        ball.addComponent(new ParticleComponent(emitter));

        getGameWorld().addEntity(ball);
    }

    private BatComponent playerBat;

    private void initPlayerBat() {
        bat1 = factory.newBat(getWidth() / 4, getHeight() / 2 - 30, true);
        getGameWorld().addEntity(bat1);

        playerBat = bat1.getComponent(BatComponent.class);
    }

    private void initEnemyBat() {
        bat2 = factory.newBat(3 * getWidth() / 4 - 20, getHeight() / 2 - 30, false);

        getGameWorld().addEntity(bat2);
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION)
    public void up() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(true, false, false));
            });
        } else {
            playerBat.up();
        }
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION)
    public void down() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, true, false));
            });
        } else {
            playerBat.down();
        }
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION_END)
    public void stopBat() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, false, true));
            });
        } else {
            playerBat.stop();
        }
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION_END)
    public void stopBat2() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, false, true));
            });
        } else {
            playerBat.stop();
        }
    }

    private void playHitAnimation(Entity bat) {
        Entities.animationBuilder()
                .autoReverse(true)
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .rotate(bat)
                .rotateFrom(FXGLMath.random(-25, 25))
                .rotateTo(0)
                .buildAndPlay();
    }

    private void showGameOver(String winner) {
        getDisplay().showMessageBox(winner + " won! Demo over\nThanks for playing", this::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
