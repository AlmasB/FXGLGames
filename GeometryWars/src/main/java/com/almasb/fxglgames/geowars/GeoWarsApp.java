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
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.SimpleGameMenu;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxglgames.geowars.collision.PlayerCrystalHandler;
import com.almasb.fxglgames.geowars.component.PlayerComponent;
import com.almasb.fxglgames.geowars.menu.GeoWarsMainMenu;
import com.almasb.fxglgames.geowars.service.HighScoreService;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getInput;
import static com.almasb.fxglgames.geowars.Config.*;
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
        var isRelease = false;

        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("1.2.1");
        settings.setIntroEnabled(isRelease);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setFullScreenAllowed(isRelease);
        settings.setFullScreenFromStart(isRelease);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(isRelease ? ApplicationMode.RELEASE : ApplicationMode.DEVELOPER);
        settings.setFontUI("game_font_7.ttf");
        settings.addEngineService(HighScoreService.class);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new GeoWarsMainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new SimpleGameMenu();
            }
        });
    }

    @Override
    protected void onPreInit() {
        // preload explosion sprite sheet
        getAssetLoader().loadTexture("explosion.png", 80 * 48, 80);

        getSettings().setGlobalSoundVolume(0.2);
        getSettings().setGlobalMusicVolume(0.5);

        loopBGM("bgm.mp3");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                playerComponent.up();
            }
        }, KeyCode.W, VirtualButton.UP);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                playerComponent.down();
            }
        }, KeyCode.S, VirtualButton.DOWN);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerComponent.left();
            }
        }, KeyCode.A, VirtualButton.LEFT);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerComponent.right();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        getInput().addAction(new UserAction("Release Shockwave") {
            @Override
            protected void onActionBegin() {
                playerComponent.releaseShockwave();
            }
        }, KeyCode.F, VirtualButton.B);

        // TODO: allow virtual button + sticks + onKey() DSL with virtual button
        getInput().addAction(new UserAction("Shoot Mouse") {
            @Override
            protected void onAction() {
                playerComponent.shoot(getInput().getMousePositionWorld());
            }
        }, MouseButton.PRIMARY);

        getInput().addAction(new UserAction("Shoot Key") {
            @Override
            protected void onAction() {

                // TODO: use sticks to aim?
                byType(WANDERER, SEEKER, RUNNER, BOUNCER)
                        .stream()
                        .min(Comparator.comparingDouble(e -> e.distance(player)))
                        .ifPresent(e -> playerComponent.shoot(e.getPosition()));
            }
        }, KeyCode.H, VirtualButton.A);

        if (!isReleaseMode()) {
            onKeyDown(KeyCode.G, () -> {

            });
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("multiplier", 1);
        vars.put("kills", 0);
        vars.put("lives", 3);
        vars.put("isRicochet", false);
        vars.put("weaponType", WeaponType.SINGLE);
    }

    @Override
    protected void initGame() {

        getGameWorld().addEntityFactory(new GeoWarsFactory());

        getGameScene().setBackgroundColor(Color.color(0, 0, 0.05, 1.0));

        spawn("Background");
        spawn("ParticleLayer");

        player = spawn("Player");
        playerComponent = player.getComponent(PlayerComponent.class);
        playerComponent.playSpawnAnimation();

        int dist = OUTSIDE_DISTANCE;

        getGameScene().getViewport().setBounds(-dist, -dist, getAppWidth() + dist, getAppHeight() + dist);
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        getWorldProperties().<Integer>addListener("multiplier", (prev, now) -> {
            WeaponType current = geto("weaponType");
            WeaponType newType = WeaponType.fromMultiplier(geti("multiplier"));

            if (newType.isBetterThan(current)) {
                set("weaponType", newType);
            }
        });

        getWorldProperties().<Integer>addListener("score", (prev, now) -> {
            getService(HighScoreService.class).setScore(now);
        });

        getWorldProperties().<Integer>addListener("lives", (prev, now) -> {
            if (now == 0)
                gameOver();
        });

        if (!IS_NO_ENEMIES) {
            int[] scoresForBombers = new int[] {
                    random(10_000, 15_000),
                    random(25_000, 30_000),
                    random(35_000, 45_000),
                    random(50_000, 55_000),
                    random(60_000, 65_000),
                    random(70_000, 75_000),
                    random(80_000, 85_000),
                    random(90_000, 95_000)
            };

            for (int i = 0; i < scoresForBombers.length; i++) {
                runOnce(() -> {
                    int bomberHeight = (int)(166 * 0.15);

                    for (int y = 0; y < getAppHeight(); y += bomberHeight) {
                        spawn("Bomber", 0, y);
                    }
                }, Duration.seconds(50 + 50 * i));
            }

//            for (int bomberScore : scoresForBombers) {
//                eventBuilder()
//                        .when(() -> geti("score") >= bomberScore)
//                        .thenRun(() -> {
//                            int bomberHeight = (int)(166 * 0.15);
//
//                            for (int y = 0; y < getAppHeight(); y += bomberHeight) {
//                                spawn("Bomber", 0, y);
//                            }
//                        })
//                        .buildAndStart();
//            }

            eventBuilder()
                    .when(() -> geti("score") >= 10_000)
                    .thenRun(() -> run(() -> spawn("Bouncer"), Duration.seconds(5)))
                    .buildAndStart();

            eventBuilder()
                    .when(() -> geti("score") >= 50_000)
                    .thenRun(() -> run(() -> spawn("Seeker"), Duration.seconds(2)))
                    .buildAndStart();

            eventBuilder()
                    .when(() -> geti("score") >= 70_000)
                    .thenRun(() -> run(() -> spawn("Runner"), Duration.seconds(3)))
                    .buildAndStart();

            eventBuilder()
                    .when(() -> geti("score") >= 10_000_000)
                    .thenRun(() -> gameOver())
                    .buildAndStart();

            run(() -> {
                for (int i = 0; i < 4; i++) {
                    spawn("Wanderer");
                }
            }, Duration.seconds(1.5));
        }
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(BULLET, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                HealthIntComponent hp = enemy.getComponent(HealthIntComponent.class);
                hp.setValue(hp.getValue() - 1);

                // TODO: duplication with shockwave
                if (hp.isZero()) {
                    if (enemy.isType(BOMBER)) {
                        spawn("Explosion", new SpawnData(enemy.getCenter()).put("numParticles", 10));
                    } else {
                        spawn("Explosion", enemy.getCenter());
                    }

                    spawn("Crystal", enemy.getCenter());

                    if (FXGLMath.randomBoolean(0.005)) {
                        spawn("ShockwavePickup", enemy.getPosition());
                    }

                    addScoreKill(enemy.getCenter());

                    enemy.removeFromWorld();
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, SEEKER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, RUNNER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOUNCER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOMBER));
        physics.addCollisionHandler(new PlayerCrystalHandler());

        CollisionHandler shockwaveEnemy = new CollisionHandler(SHOCKWAVE, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity wave, Entity enemy) {
                HealthIntComponent hp = enemy.getComponent(HealthIntComponent.class);
                hp.setValue(hp.getValue() - 1);

                if (hp.isZero()) {
                    if (enemy.isType(BOMBER)) {
                        spawn("Explosion", new SpawnData(enemy.getCenter()).put("numParticles", 10));
                    } else {
                        spawn("Explosion", enemy.getCenter());
                    }

                    spawn("Crystal", enemy.getCenter());

                    if (FXGLMath.randomBoolean(0.005)) {
                        spawn("ShockwavePickup", enemy.getPosition());
                    }

                    addScoreKill(enemy.getCenter());

                    enemy.removeFromWorld();
                }
            }
        };

        physics.addCollisionHandler(shockwaveEnemy);
        physics.addCollisionHandler(shockwaveEnemy.copyFor(SHOCKWAVE, SEEKER));
        physics.addCollisionHandler(shockwaveEnemy.copyFor(SHOCKWAVE, RUNNER));
        physics.addCollisionHandler(shockwaveEnemy.copyFor(SHOCKWAVE, BOUNCER));
        physics.addCollisionHandler(shockwaveEnemy.copyFor(SHOCKWAVE, BOMBER));

        CollisionHandler playerEnemy = new CollisionHandler(PLAYER, WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {

                getGameScene().getViewport().shakeTranslational(8);

                // TODO: rewrite to be all but player grid etc.
                byType(WANDERER, SEEKER, RUNNER, BOUNCER, BOMBER, BULLET, CRYSTAL, SHOCKWAVE_PICKUP)
                        .forEach(Entity::removeFromWorld);

                player.setPosition(getAppWidth() / 2, getAppHeight() / 2);
                playerComponent.playSpawnAnimation();

                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, SEEKER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, RUNNER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, BOUNCER));
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, BOMBER));

        onCollisionCollectible(PLAYER, SHOCKWAVE_PICKUP, (pickup) -> {
            playerComponent.setShockwaveReady(true);
        });
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

        var livesText = getUIFactoryService().newText("", Color.WHITE, 24.0);
        livesText.setTranslateX(60);
        livesText.setTranslateY(110);
        livesText.textProperty().bind(getip("lives").asString("Lives: %d"));

        var ricochetText = getUIFactoryService().newText("RICOCHET", Color.ANTIQUEWHITE, 16.0);
        ricochetText.setTranslateX(60);
        ricochetText.setTranslateY(130);
        ricochetText.visibleProperty().bind(getbp("isRicochet"));

        getGameScene().addUINodes(multText, scoreText, livesText, ricochetText);

        Text goodLuck = getUIFactoryService().newText("Score as many points as you can. Good luck!", Color.AQUA, 38);

        addUINode(goodLuck);

        centerText(goodLuck);

        animationBuilder()
                .duration(Duration.seconds(2))
                .autoReverse(true)
                .repeat(2)
                .fadeIn(goodLuck)
                .buildAndPlay();
    }

    private void addScoreKill(Point2D enemyPosition) {
        inc("kills", +1);

        if (geti("kills") == 15) {
            set("kills", 0);
            inc("multiplier", +1);
        }

        final int multiplier = geti("multiplier");

        inc("score", +10*multiplier);

        var shadow = new DropShadow(25, Color.WHITE);

        Text bonusText = getUIFactoryService().newText("+10" + (multiplier == 1 ? "" : "x" + multiplier), Color.color(1, 1, 1, 0.8), 24);
        bonusText.setStroke(Color.GOLD);
        bonusText.setEffect(shadow);
        bonusText.setCache(true);
        bonusText.setCacheHint(CacheHint.SPEED);

        var e = entityBuilder()
                .at(enemyPosition)
                .view(bonusText)
                .zIndex(2000)
                .buildAndAttach();

        animationBuilder()
                .onFinished(() -> e.removeFromWorld())
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(enemyPosition)
                .to(enemyPosition.subtract(0, 65))
                .buildAndPlay();

        animationBuilder()
                .duration(Duration.seconds(0.35))
                .autoReverse(true)
                .repeat(2)
                .interpolator(Interpolators.BOUNCE.EASE_IN())
                .scale(e)
                .from(new Point2D(1, 1))
                .to(new Point2D(1.2, 0.85))
                .buildAndPlay();
    }

    private void deductScoreDeath() {
        inc("lives", -1);
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

    private void gameOver() {
        getDialogService().showInputBox("Your score:" + geti("score") + "\nEnter your name", s -> s.matches("[a-zA-Z]*"), name -> {
            getService(HighScoreService.class).commit(name);

            getSaveLoadService().saveAndWriteTask(SAVE_FILE_NAME).run();

            getGameController().gotoMainMenu();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
