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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.view.ScrollingBackgroundView;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.spacerunner.collision.BulletEnemyHandler;
import com.almasb.fxglgames.spacerunner.collision.PlayerBulletHandler;
import com.almasb.fxglgames.spacerunner.control.PlayerComponent;
import com.almasb.fxglgames.spacerunner.level.Level;
import javafx.beans.binding.Bindings;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerApp extends GameApplication {

    private PlayerComponent playerComponent;
    private Level level;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Runner");
        settings.setVersion("0.1");
        settings.setWidth(1000);
        settings.setHeight(700);
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
                playerComponent.up();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerComponent.down();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Change Weapon") {
            @Override
            protected void onAction() {
                playerComponent.changeWeapon();

                weaponTexture.setImage(image("sprite_laser.png"));
                bullets.textProperty().bind(getip("laser").asString("x %d"));
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                playerComponent.shoot();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("bullets", 999);
        vars.put("laser", 50);
        vars.put("rockets", 10);
        vars.put("heat", 0);
        vars.put("overheating", false);
        vars.put("shield", 0);
        vars.put("hasShield", false);
    }

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalMusicVolume(0.3);
        getAudioPlayer().setGlobalSoundVolume(0.4);

        getGameWorld().addEntityFactory(new SpaceRunnerFactory());

        Texture t = getAssetLoader().loadTexture("bg_0.png");

        getGameScene().addGameView(new ScrollingBackgroundView(t.superTexture(t, HorizontalDirection.RIGHT),
                Orientation.HORIZONTAL));

        Entity player = getGameWorld().spawn("Player", 50, getHeight() / 2);

        playerComponent = player.getComponent(PlayerComponent.class);

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getHeight());
        getGameScene().getViewport().bindToEntity(player, 50, getHeight() / 2);

        getGameState().<Integer>addListener("heat", (prev, now) -> {
            if (now >= 100) {
                set("overheating", true);
            }

            if (now == 0) {
                set("overheating", false);
            }
        });

        getbp("hasShield").bind(getip("shield").isEqualTo(100));

        run(this::spawnWaveIfNeeded, Duration.seconds(1));

        run(() -> {
            if (geti("heat") > 0)
                inc("heat", -5);

            if (geti("bullets") < 999)
                inc("bullets", +1);

        }, Duration.seconds(0.25));

        run(() -> {
            if (geti("shield") < 100)
                inc("shield", +4);
        }, Duration.seconds(0.5));

        nextLevel();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerBulletHandler());
    }

    private Texture weaponTexture;
    private Text bullets;

    @Override
    protected void initUI() {
        weaponTexture = texture("sprite_bullet.png", 22, 11);

        bullets = getUIFactory().newText("", Color.rgb(20, 20, 20), 16);
        bullets.textProperty().bind(getip("bullets").asString("x %d"));

        HBox ui = new HBox(15,
                weaponTexture,
                bullets
                );

        Text laser = getUIFactory().newText("", Color.rgb(20, 20, 20), 16);
        laser.textProperty().bind(getip("laser").asString("x %d"));

        HBox ui2 = new HBox(15,
                texture("sprite_laser.png"),
                laser
        );

        Text rockets = getUIFactory().newText("", Color.rgb(20, 20, 20), 16);
        rockets.textProperty().bind(getip("rockets").asString("x %d"));

        HBox ui3 = new HBox(15,
                texture("rocket.png", 30, 8),
                rockets
        );

        VBox boxWeapons = new VBox(15, ui, ui2, ui3);
        boxWeapons.setTranslateX(getWidth() - 150);
        boxWeapons.setTranslateY(550);
        boxWeapons.setScaleX(1.4);
        boxWeapons.setScaleY(1.4);

        Texture uiBorder = texture("ui.png");
        uiBorder.setTranslateY(getHeight() - uiBorder.getHeight());

        getGameScene().addUINode(uiBorder);

        ProgressBar barHP = new ProgressBar(false);
        barHP.setHeight(30.0);
        barHP.setLabelVisible(false);


        barHP.setFill(Color.GREEN);
        barHP.setBackgroundFill(Color.DARKGREY);
        barHP.setTraceFill(Color.LIGHTGREEN);
        barHP.currentValueProperty().bind(playerComponent.getEntity().getComponent(HealthComponent.class).valueProperty());

        // heat

        ProgressBar barHeat = new ProgressBar(false);
        barHeat.setHeight(30.0);
        barHeat.setLabelVisible(false);

        barHeat.setFill(Color.RED);
        barHeat.setBackgroundFill(Color.DARKGREY);
        barHeat.setTraceFill(Color.YELLOW);
        barHeat.currentValueProperty().bind(getip("heat"));

        ProgressBar barShield = new ProgressBar(false);
        barShield.setHeight(30.0);
        barShield.setLabelVisible(false);

        barShield.setFill(Color.BLUE);
        barShield.setBackgroundFill(Color.DARKGREY);
        barShield.setTraceFill(Color.LIGHTBLUE);
        barShield.currentValueProperty().bind(getip("shield"));

        Text textShield = getUIFactory().newText("Shield");
        textShield.setFill(Color.BLUE);
        textShield.visibleProperty().bind(getbp("hasShield"));

        Text textDanger = getUIFactory().newText("Danger!");
        textDanger.fillProperty().bind(
                Bindings.when(getbp("overheating")).then(Color.RED).otherwise(Color.DARKGREY)
        );
        textDanger.opacityProperty().bind(
                Bindings.when(getbp("overheating")).then(1.0).otherwise(0.5)
        );

        HBox symbols = new HBox(10, textShield, textDanger);
        symbols.setTranslateX(25);

        VBox bars = new VBox(-10, barHP, barShield, barHeat, symbols);
        bars.setTranslateX(0);
        bars.setTranslateY(520);

        getGameScene().addUINodes(bars, boxWeapons);

        play("player_1.wav");

        runOnce(() -> play("begin.wav"), Duration.seconds(1));
    }

    private void nextLevel() {
        level = new Level();

        level.spawnNewWave();
    }

    private void spawnWaveIfNeeded() {
        if (!getGameWorld().getEntitiesByType(SpaceRunnerType.ENEMY).isEmpty())
            return;

        if (level.isDone()) {
            nextLevel();
        } else {
            level.spawnNewWave();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
