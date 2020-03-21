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
import com.almasb.fxgl.animation.AnimationBuilder;
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
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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

    private static final int MAX_LEVEL = 5;
    private static final int STARTING_LEVEL = 1;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Breakout");
        settings.setVersion("2.0");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("main_font.ttf");
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private Text debugText;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalMusicVolume(0);
        getSettings().setGlobalSoundVolume(0);

        if (!getSettings().isExperimentalNative()) {
            loopBGM("BGM.mp3");
        }
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

        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
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

        spawn("ball", getAppWidth() / 2, getAppHeight() - 250);

        spawn("bat", getAppWidth() / 2, getAppHeight() - 180);

        animateCamera(getBallControl()::release);
    }

    private Animation<Double> cameraAnimation;

    private void animateCamera(Runnable onAnimationFinished) {
        AnimatedValue<Double> value = new AnimatedValue<>(getAppHeight() * 1.0, 0.0);
        cameraAnimation = new AnimationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(onAnimationFinished::run)
                .build(value, y -> getGameScene().getViewport().setY(y));

        cameraAnimation.start();
    }

    private void playHitSound() {
        if (!getSettings().isExperimentalNative()) {
            play("hit.wav");
        }
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, BRICK, (ball, brick) -> {

            playHitSound();

            if (!getBallControl().getColor().equals(brick.getComponent(BrickComponent.class).getColor())) {
                return;
            }

            ball.call("onHit");
            brick.call("onHit");

            spawn("sparks", new SpawnData(ball.getPosition()).put("color", getBallControl().getColor()));

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
                    inc("score", -100);
                }

                getGameScene().getViewport().shakeTranslational(1.5);
            }
        });
    }

    @Override
    protected void initUI() {
        debugText = new Text();
        debugText.setFill(Color.WHITE);

        var textScore = getUIFactoryService().newText(getip("score").asString());

        addUINode(textScore, 50, getAppHeight() - 20);
        addUINode(debugText, 50, 50);

        // touch based controls (e.g. mobile screen)
        var circleLeft = new Circle(75);
        circleLeft.setStroke(Color.WHITE);
        circleLeft.setStrokeWidth(2.5);
        circleLeft.setOnMouseClicked(e -> {
            getInput().mockKeyPress(KeyCode.SPACE);
            getInput().mockKeyRelease(KeyCode.SPACE);
        });

        getBallControl().colorProperty().addListener((obs, old, newValue) -> {
            circleLeft.setFill(getBallControl().getNextColor());
        });

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
        addUINode(circleLeft, 100, getAppHeight() - 120);
        
        runOnce(() -> {
            getSceneService().pushSubScene(new TutorialSubScene());

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
