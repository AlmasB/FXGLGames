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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import javafx.animation.PathTransition;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.QuadCurve;
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

    private BatComponent getBatControl() {
        return getGameWorld().getSingleton(BAT).getComponent(BatComponent.class);
    }

    private BallComponent getBallControl() {
        return getGameWorld().getSingleton(BALL).getComponent(BallComponent.class);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Breakout Underwater");
        settings.setVersion("2.0");
        settings.setWidth(14 * 96);
        settings.setHeight(22 * 32);
        settings.setDeveloperMenuEnabled(true);
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
    }

    @Override
    protected void initGame() {
        initBackground();

        getGameWorld().addEntityFactory(new BreakoutFactory());

        setLevelFromMap("tmx/level1.tmx");

        spawn("ball", getAppWidth() / 2, getAppHeight() - 250);

        spawn("bat", getAppWidth() / 2, getAppHeight() - 150);

        getBallControl().release();
    }

    private void initBackground() {
        // we add IrremovableComponent because regardless of the level
        // the background and screen bounds stay in the game world
        entityBuilder()
                .view(new Rectangle(getAppWidth(), getAppHeight()))
                .with(new IrremovableComponent())
                .zIndex(-1)
                .buildAndAttach();

        entityBuilder()
                .type(WALL)
                .collidable()
                .with(new IrremovableComponent())
                .buildScreenBoundsAndAttach(40);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, BRICK, (ball, brick) -> {
            brick.getComponent(BrickComponent.class).onHit();
        });

        onCollisionBegin(BALL, WALL, (ball, wall) -> {
            getGameScene().getViewport().shakeTranslational(1.5);
        });
    }

    @Override
    protected void initUI() {
//        Text text = getUIFactory().newText("Level 1", Color.WHITE, 48);
//        getGameScene().addUINode(text);
//
//        QuadCurve curve = new QuadCurve(-100, 0, getAppWidth() / 2, getAppHeight(), getAppWidth() + 100, 0);
//
//        PathTransition transition = new PathTransition(Duration.seconds(4), curve, text);
//        transition.setOnFinished(e -> {
//            getGameScene().removeUINode(text);
//            getBallControl().release();
//        });
//        transition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
