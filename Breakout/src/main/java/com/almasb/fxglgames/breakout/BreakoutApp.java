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

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.breakout.BreakoutType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Breakout");
        settings.setVersion("2.0");
        settings.setWidth(14 * 96);
        settings.setHeight(22 * 32);
        settings.setFontUI("main_font.ttf");
        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void onPreInit() {
        //loopBGM("BGM.mp3");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                getBatControl().left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                getBatControl().right();
            }
        }, KeyCode.D);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("lives", 3);
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        initBackground();

        getGameWorld().addEntityFactory(new BreakoutFactory());

        setLevelFromMap("tmx/level1.tmx");

        spawn("ball", getAppWidth() / 2, getAppHeight() - 250);

        spawn("bat", getAppWidth() / 2, getAppHeight() - 180);

        animateCamera(getBallControl()::release);
    }

    private void initBackground() {
        getGameScene().setBackgroundColor(Color.BLACK);

        // we add IrremovableComponent because regardless of the level
        // the screen bounds stay in the game world
        entityBuilder()
                .type(WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }

    private void animateCamera(Runnable onAnimationFinished) {
        var camera = new Entity();
        camera.setY(getAppHeight());

        getGameScene().getViewport().yProperty().bind(camera.yProperty());

        animationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(() -> {
                    getGameScene().getViewport().yProperty().unbind();
                    onAnimationFinished.run();
                })
                .translate(camera)
                .from(new Point2D(0, camera.getY()))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, BRICK, (ball, brick) -> {
            ball.call("onHit");
            brick.call("onHit");

            spawn("sparks", ball.getPosition());

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
            ball.call("applySlow");
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BALL, WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("BOT")) {
                    inc("score", -100);
                }

                getGameScene().getViewport().shakeTranslational(1.5);
            }
        });
    }

    @Override
    protected void initUI() {
        var textScore = getUIFactory().newText("", 24);
        textScore.textProperty().bind(getip("score").asString());

        addUINode(textScore, 50, getAppHeight() - 20);
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
