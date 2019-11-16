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
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.breakout.components.BallComponent;
import com.almasb.fxglgames.breakout.components.BatComponent;
import com.almasb.fxglgames.breakout.components.BrickComponent;
import javafx.animation.PathTransition;
import javafx.geometry.Point2D;
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

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutApp extends GameApplication {

    private BatComponent getBatControl() {
        return getGameWorld().getSingleton(BreakoutType.BAT).getComponent(BatComponent.class);
    }

    private BallComponent getBallControl() {
        return getGameWorld().getSingleton(BreakoutType.BALL).getComponent(BallComponent.class);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Breakout Underwater");
        settings.setVersion("0.2");
        settings.setWidth(600);
        settings.setHeight(800);
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
        initBGM();
        initLevel();
        initBubbles();
    }

    private void initBGM() { 
        loopBGM("BGM01.wav");
    }

    private void initLevel() {
        initBackground();

        getGameWorld().addEntityFactory(new BreakoutFactory());

        Level level = getAssetLoader().loadLevel("level1.txt", new TextLevelLoader(40, 40, '0'));

        getGameWorld().setLevel(level);
    }

    private void initBackground() {
        Rectangle bg0 = new Rectangle(getAppWidth(), getAppHeight(),
                new LinearGradient(getAppWidth() / 2, 0, getAppWidth() / 2, getAppHeight(),
                        false, CycleMethod.NO_CYCLE,
                        new Stop(0.2, Color.AQUA), new Stop(0.8, Color.BLACK)));

        Rectangle bg1 = new Rectangle(getAppWidth(), getAppHeight(), Color.color(0, 0, 0, 0.2));
        bg1.setBlendMode(BlendMode.DARKEN);

        // we add IrremovableComponent because regardless of the level
        // the background and screen bounds stay in the game world
        entityBuilder()
                .view(bg0)
                .view(bg1)
                .with(new IrremovableComponent())
                .zIndex(-1)
                .buildAndAttach();

        Entity screenBounds = entityBuilder().buildScreenBounds(40);
        screenBounds.addComponent(new IrremovableComponent());

        getGameWorld().addEntity(screenBounds);
    }

    private void initBubbles() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setSourceImage(getAssetLoader().loadTexture("bubble.png").getImage());
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setEmissionRate(0.25);
        emitter.setExpireFunction(i -> Duration.seconds(3));
        emitter.setVelocityFunction(i -> new Point2D(0, -FXGLMath.random(2f, 4f) * 60));
        emitter.setSpawnPointFunction(i -> new Point2D(FXGLMath.random(0, (float)getAppWidth()), -20 + FXGLMath.random(0, 50)));
        emitter.setScaleFunction(i -> new Point2D(FXGLMath.random(-0.05f, 0), FXGLMath.random(-0.05f, 0)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_IN());

        Entity bubbles = new Entity();
        bubbles.translateY(getAppHeight());
        bubbles.addComponent(new ParticleComponent(emitter));

        getGameWorld().addEntity(bubbles);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BreakoutType.BALL, BreakoutType.BRICK) {
            @Override
            protected void onCollisionBegin(Entity ball, Entity brick) {
                brick.getComponent(BrickComponent.class).onHit();
            }
        });
    }

    @Override
    protected void initUI() {
        Text text = getUIFactory().newText("Level 1", Color.WHITE, 48);
        getGameScene().addUINode(text);

        QuadCurve curve = new QuadCurve(-100, 0, getAppWidth() / 2, getAppHeight(), getAppWidth() + 100, 0);

        PathTransition transition = new PathTransition(Duration.seconds(4), curve, text);
        transition.setOnFinished(e -> {
            getGameScene().removeUINode(text);
            getBallControl().release();
        });
        transition.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
