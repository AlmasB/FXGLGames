/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxglgames.geowars.collision.BulletPortalHandler;
import com.almasb.fxglgames.geowars.collision.PlayerCrystalHandler;
import com.almasb.fxglgames.geowars.component.HealthComponent;
import com.almasb.fxglgames.geowars.component.PlayerComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.almasb.fxglgames.geowars.GeoWarsType.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GeoWarsApp extends GameApplication {

    private Entity player;
    private PlayerComponent playerComponent;

    public Entity getPlayer() {
        return player;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("1.0");
        settings.setConfigClass(GeoWarsConfig.class);

        if (!settings.isExperimentalNative()) {
            settings.setFontUI("game_font_7.ttf");
        }
    }

    @Override
    protected void onPreInit() {
        // preload explosion sprite sheet
        getAssetLoader().loadTexture("explosion.png", 80 * 48, 80);

        getSettings().setGlobalSoundVolume(0.2);
        getSettings().setGlobalMusicVolume(0.2);

        loopBGM("bgm.mp3");
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        onKey(KeyCode.W, () -> playerComponent.up());
        onKey(KeyCode.A, () -> playerComponent.left());
        onKey(KeyCode.S, () -> playerComponent.down());
        onKey(KeyCode.D, () -> playerComponent.right());

        // TODO: add the same API as above
        //onKeyDown(KeyCode.E, "", Runnable { });

        onBtn(MouseButton.PRIMARY, "Shoot", () -> playerComponent.shoot(input.getMousePositionWorld()));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("multiplier", 1);
        vars.put("kills", 0);
        vars.put("time", 120);
        vars.put("weaponType", WeaponType.SINGLE);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GeoWarsFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        spawn("Background");
        player = spawn("Player");
        playerComponent = player.getComponent(PlayerComponent.class);

        getWorldProperties().<Integer>addListener("multiplier", (prev, now) -> {
            WeaponType current = geto("weaponType");
            WeaponType newType = WeaponType.fromMultiplier(geti("multiplier"));

            if (newType.isBetterThan(current)) {
                set("weaponType", newType);
            }
        });

        getWorldProperties().<Integer>addListener("time", (prev, now) -> {
            if (now == 0)
                getDialogService().showMessageBox("Demo Over. Your score: " + geti("score"), getGameController()::exit);
        });

        getGameTimer().runAtInterval(() -> spawn("Wanderer"), Duration.seconds(1.5));
        getGameTimer().runAtInterval(() -> spawn("Seeker"), Duration.seconds(3));
//        getGameTimer().runAtInterval(() -> spawn("Runner"), Duration.seconds(5));
        getGameTimer().runAtInterval(() -> spawn("Bouncer"), Duration.seconds(5));
//        getGameTimer().runAtInterval(() -> spawn("Portal", getRandomPoint()), Duration.seconds(5));
        getGameTimer().runAtInterval(() -> inc("time", -1), Duration.seconds(1));
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(BULLET, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                HealthComponent hp = enemy.getComponent(HealthComponent.class);
                hp.setValue(hp.getValue() - 1);

                if (hp.getValue() == 0) {
                    onDeath(enemy);
                    enemy.removeFromWorld();
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, SEEKER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, RUNNER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOUNCER));
        physics.addCollisionHandler(new BulletPortalHandler());
        physics.addCollisionHandler(new PlayerCrystalHandler());

        CollisionHandler playerEnemy = new CollisionHandler(PLAYER, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {

                getGameScene().getViewport().shakeTranslational(8);

                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, SEEKER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, RUNNER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, BOUNCER));
    }

    @Override
    protected void initUI() {
        Text scoreText = getUIFactoryService().newText("", Color.WHITE, 28);
        scoreText.setTranslateX(60);
        scoreText.setTranslateY(70);
        scoreText.textProperty().bind(getip("score").asString());
        scoreText.setStroke(Color.GOLD);

        Text multText = getUIFactoryService().newText("", Color.WHITE, 28);
        multText.setTranslateX(60);
        multText.setTranslateY(90);
        multText.textProperty().bind(getip("multiplier").asString("x %d"));

        Text timerText = getUIFactoryService().newText("", Color.WHITE, 28);
        timerText.layoutBoundsProperty().addListener((o, old, bounds) -> {
            timerText.setTranslateX(getAppWidth() / 2 - bounds.getWidth() / 2);
        });

        timerText.setTranslateX(getAppWidth() / 2);
        timerText.setTranslateY(60);
        timerText.textProperty().bind(getip("time").asString());

        Circle timerCircle = new Circle(40, 40, 40, null);
        timerCircle.setStrokeWidth(2);
        timerCircle.setStroke(Color.AQUA);
        timerCircle.setTranslateX(getAppWidth() / 2 - 40);
        timerCircle.setTranslateY(60 - 40 - 5);

        getGameScene().addUINodes(multText, scoreText, timerText, timerCircle);

        Text beware = getUIFactoryService().newText("Beware! Seekers get smarter every spawn!", Color.AQUA, 38);
        beware.setOpacity(0);

        addUINode(beware);

        centerText(beware);

        animationBuilder()
                .duration(Duration.seconds(2))
                .autoReverse(true)
                .repeat(2)
                .fadeIn(beware)
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(0.35))
                .autoReverse(true)
                .repeatInfinitely()
                .scale(timerCircle)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.1, 1.1))
                .buildAndPlay();
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getAppWidth(), Math.random() * getAppHeight());
    }

    private void addScoreKill(Point2D enemyPosition) {
        inc("kills", +1);

        if (geti("kills") == 15) {
            set("kills", 0);
            inc("multiplier", +1);
        }

        final int multiplier = geti("multiplier");

        inc("score", +100*multiplier);

        var shadow = new DropShadow(25, Color.WHITE);

        Text bonusText = getUIFactoryService().newText("+100" + (multiplier == 1 ? "" : "x" + multiplier), Color.color(1, 1, 1, 0.8), 24);
        bonusText.setStroke(Color.GOLD);
        bonusText.setEffect(shadow);

        addUINode(bonusText, enemyPosition.getX(), enemyPosition.getY());

        animationBuilder()
                .onFinished(() -> removeUINode(bonusText))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(bonusText)
                .from(enemyPosition)
                .to(enemyPosition.subtract(0, 65))
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(0.35))
                .autoReverse(true)
                .repeat(2)
                .interpolator(Interpolators.BOUNCE.EASE_IN())
                .scale(bonusText)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.2, 0.85))
                .buildAndPlay();
    }

    private void deductScoreDeath() {
        inc("score", -1000);
        set("kills", 0);
        set("multiplier", 1);

        Text bonusText = getUIFactoryService().newText("-1000", Color.WHITE, 20);

        addUINode(bonusText, 1100, 70);

        animationBuilder()
                .duration(Duration.seconds(0.5))
                .onFinished(() -> {
                    removeUINode(bonusText);
                })
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .translate(bonusText)
                .from(new Point2D(bonusText.getTranslateX(), bonusText.getTranslateY()))
                .to(new Point2D(bonusText.getTranslateX(), 0))
                .buildAndPlay();
    }

    public void onDeath(Entity entity) {
        spawn("Explosion", entity.getCenter());
        spawn("Crystal", entity.getCenter());

        addScoreKill(entity.getCenter());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
