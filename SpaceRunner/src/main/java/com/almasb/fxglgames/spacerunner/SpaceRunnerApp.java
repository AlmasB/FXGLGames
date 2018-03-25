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

package com.almasb.fxglgames.spacerunner;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.view.ScrollingBackgroundView;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spacerunner.collision.BulletEnemyHandler;
import com.almasb.fxglgames.spacerunner.control.PlayerControl;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.loopBGM;
import static com.almasb.fxgl.app.DSLKt.run;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Runner");
        settings.setVersion("0.1");
        settings.setWidth(1000);
        settings.setHeight(500);
        settings.setConfigClass(GameConfig.class);
    }

    @Override
    protected void preInit() {
        loopBGM("bgm.mp3");
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
            protected void onAction() {
                playerControl.shoot();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameWorld().setEntityFactory(new SpaceRunnerFactory());

        Texture t = getAssetLoader().loadTexture("bg_0.png");

        getGameScene().addGameView(new ScrollingBackgroundView(t.superTexture(t, HorizontalDirection.RIGHT),
                Orientation.HORIZONTAL));

        Entity player = getGameWorld().spawn("Player", 50, getHeight() / 2);

        playerControl = player.getControl(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getHeight());
        getGameScene().getViewport().bindToEntity(player, 50, getHeight() / 2);

        run(this::spawnWave, Duration.seconds(5));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
    }

    private void spawnWave() {
        for (int i = 0; i < 10; i++) {
            Entity e = getGameWorld().spawn("Enemy1", playerControl.getEntity().getX() + FXGLMath.random(600, 800), FXGLMath.random(100, 400));
            e.setScaleX(0);
            e.setScaleY(0);
            Entities.animationBuilder()
                    .duration(Duration.seconds(FXGLMath.random(2, 3.0)))
                    .interpolator(Interpolators.ELASTIC.EASE_OUT())
                    .scale(e)
                    .from(new Point2D(0, 0))
                    .to(new Point2D(1, 1))
                    .buildAndPlay();

            Entities.animationBuilder()
                    .autoReverse(true)
                    .repeat(2)
                    .duration(Duration.seconds(FXGLMath.random(1.2, 2.0)))
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .rotate(e)
                    .rotateFrom(0)
                    .rotateTo(FXGLMath.random(180, 360))
                    .buildAndPlay();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
