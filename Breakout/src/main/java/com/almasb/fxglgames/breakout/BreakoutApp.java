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

package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.breakout.components.BackgroundStarsViewComponent;
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import javafx.beans.binding.Bindings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.breakout.BreakoutType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutApp extends GameApplication {

    public static final int WIDTH = 14 * 96;
    public static final int HEIGHT = 22 * 32;

    private static final int MAX_LEVEL = 10;
    private static final int STARTING_LEVEL = 1;

    private Circle uiCircle;
    private MultiplierView uiMultiplier;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Breakout");
        settings.setVersion("2.1");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("main_font.ttf");
        settings.setFontGame("ENDORALT.ttf");
        settings.setIntroEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0.5);
        getSettings().setGlobalSoundVolume(0.5);

        loopBGM("BGM.mp3");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                getBatControl().left();
            }

            @Override
            protected void onActionEnd() {
                getBatControl().stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                getBatControl().right();
            }

            @Override
            protected void onActionEnd() {
                getBatControl().stop();
            }
        }, KeyCode.D);

        onKeyDown(KeyCode.SPACE, "Change color", () -> getBallControl().changeColorToNext());

        if (!isReleaseMode()) {
            onKeyDown(KeyCode.L, "Next level", () -> nextLevel());

            onKeyDown(KeyCode.K, "Print", () -> {
                byType(BACKGROUND).get(0).getComponent(BackgroundStarsViewComponent.class).onUpdate(0.016);
                System.out.println();
            });
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("lives", 3);
        vars.put("score", 0);
        vars.put("multiplier", 0);
        vars.put("level", STARTING_LEVEL);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BreakoutFactory());

        initBackground();

        setLevel(STARTING_LEVEL);
    }

    private void initBackground() {
        getGameScene().setBackgroundColor(Color.BLACK);

        spawn("background");
        spawn("colorCircle", -200, getAppHeight() - 200);

        // we add IrremovableComponent because regardless of the level
        // the screen bounds stay in the game world
        entityBuilder()
                .type(WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }

    private void nextLevel() {
        inc("level", +1);
        var levelNum = geti("level");

        if (levelNum > MAX_LEVEL) {
            getDialogService().showMessageBox("You have completed demo!", getGameController()::exit);
            return;
        }

        setLevel(levelNum);
    }

    private void setLevel(int levelNum) {
        getGameWorld().getEntitiesCopy().forEach(e -> e.removeFromWorld());
        setLevelFromMap("tmx/level" + levelNum + ".tmx");

        var ball = spawn("ball", getAppWidth() / 2, getAppHeight() - 250);
        ball.getComponent(BallComponent.class).colorProperty().addListener((obs, old, newValue) -> {
            uiCircle.setFill(getBallControl().getNextColor());
        });

        spawn("bat", getAppWidth() / 2, getAppHeight() - 180);

        animateCamera(() -> {
            getSceneService().pushSubScene(new NewLevelSubScene(levelNum));
            getBallControl().release();
        });
    }

    private Animation<Double> cameraAnimation;

    private void animateCamera(Runnable onAnimationFinished) {
        AnimatedValue<Double> value = new AnimatedValue<>(getAppHeight() * 1.0, 0.0);
        cameraAnimation = animationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(onAnimationFinished::run)
                .animate(value)
                .onProgress(y -> getGameScene().getViewport().setY(y))
                .build();

        cameraAnimation.start();
    }

    private void playHitSound() {
        play("hit.wav");
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, BRICK, (ball, brick) -> {

            playHitSound();

            if (!getBallControl().getColor().equals(brick.getComponent(BrickComponent.class).getColor())) {
                set("multiplier", 0);
                return;
            }

            ball.call("onHit");
            brick.call("onHit");

            spawn("sparks", new SpawnData(ball.getPosition()).put("color", getBallControl().getColor()));

            inc("multiplier", +1);
            // TODO: use mult to compute score
            inc("score", +50);

            if (FXGLMath.randomBoolean()) {
                spawn("powerup", brick.getPosition());
            }
        });

        onCollisionCollectible(BAT, POWERUP, powerup -> {
            PowerupType type = powerup.getObject("powerupType");

            getBallControl().applyPowerup(type);

        });

        onCollisionBegin(BULLET_BALL, BRICK, (ball, brick) -> {
            ball.removeFromWorld();
            brick.call("onHit");
        });

        onCollisionBegin(BAT, BALL, (bat, ball) -> {
            playHitSound();

            ball.call("applySlow");
            bat.call("onHit");
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BALL, WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                playHitSound();

                if (boxB.getName().equals("BOT")) {
                    set("multiplier", 0);
                    inc("score", -100);
                }

                getGameScene().getViewport().shakeTranslational(1.5);
            }
        });
    }

    @Override
    protected void initUI() {
        var textScore = getUIFactoryService().newText(getip("score").asString());

        addUINode(textScore, 220, getAppHeight() - 20);

        var regionLeft = new Rectangle(getAppWidth() / 2, getAppHeight());
        regionLeft.setOpacity(0);
        regionLeft.setOnMousePressed(e -> getInput().mockKeyPress(KeyCode.A));
        regionLeft.setOnMouseReleased(e -> getInput().mockKeyRelease(KeyCode.A));

        var regionRight = new Rectangle(getAppWidth() / 2, getAppHeight());
        regionRight.setOpacity(0);
        regionRight.setOnMousePressed(e -> getInput().mockKeyPress(KeyCode.D));
        regionRight.setOnMouseReleased(e -> getInput().mockKeyRelease(KeyCode.D));

        addUINode(regionLeft);
        addUINode(regionRight, getAppWidth() / 2, 0);

        var radius = 200;

        // touch based color control (e.g. mobile screen)
        uiCircle = new Circle(radius, radius, radius);
        uiCircle.setStroke(Color.WHITE);
        uiCircle.setStrokeWidth(3.5);
        uiCircle.setOnMouseClicked(e -> {
            getInput().mockKeyPress(KeyCode.SPACE);
            getInput().mockKeyRelease(KeyCode.SPACE);
        });

        addUINode(uiCircle, -200, getAppHeight() - 200);

        uiMultiplier = new MultiplierView();
        uiMultiplier.getAssessmentText().textProperty().bind(
                Bindings.createStringBinding(() -> {
                    int mult = geti("multiplier");

                    if (mult > 15) {
                        return "LEGENDARY";
                    } else if (mult > 10) {
                        return "EPIC";
                    } else if (mult > 6) {
                        return "AWESOME";
                    } else if (mult > 2) {
                        return "GOOD";
                    }

                    return "";
                }, getip("multiplier"))
        );

        uiMultiplier.getAssessmentText().strokeProperty().bind(
                uiMultiplier.getAssessmentText().fillProperty()
        );

        uiMultiplier.getAssessmentText().fillProperty().bind(
                Bindings.createObjectBinding(() -> {
                    int mult = geti("multiplier");

                    if (mult > 15) {
                        return Color.RED;
                    } else if (mult > 10) {
                        return Color.LIGHTPINK;
                    } else if (mult > 6) {
                        return Color.GREENYELLOW;
                    } else if (mult > 2) {
                        return Color.GREEN;
                    }

                    return Color.GREEN;
                }, getip("multiplier"))
        );

        uiMultiplier.getText().fillProperty().bind(uiMultiplier.getAssessmentText().fillProperty());
        uiMultiplier.getText().textProperty().bind(getip("multiplier").asString("x%d"));
        uiMultiplier.visibleProperty().bind(getip("multiplier").greaterThan(0));

        addUINode(uiMultiplier, getAppWidth() - 140, -60);

        getWorldProperties().<Integer>addListener("multiplier", (old, now) -> {
            int mult = now;
            int prev = old;

            if (mult > 0 && mult != prev) {
                uiMultiplier.playAnimation();
            }
        });

        runOnce(() -> {
            if (isReleaseMode()) {
                getSceneService().pushSubScene(new TutorialSubScene());
            }

            runOnce(() -> getBallControl().changeColorToNext(), Duration.seconds(0.016));

        }, Duration.seconds(0.5));
    }

    @Override
    protected void onUpdate(double tpf) {
        cameraAnimation.onUpdate(tpf);

        if (byType(BRICK).isEmpty()) {
            nextLevel();
        }
    }

    private BatComponent getBatControl() {
        return getGameWorld().getSingleton(BAT).getComponent(BatComponent.class);
    }

    private BallComponent getBallControl() {
        return getGameWorld().getSingleton(BALL).getComponent(BallComponent.class);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
