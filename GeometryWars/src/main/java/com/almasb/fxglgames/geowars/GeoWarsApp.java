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
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.SimpleGameMenu;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualJoystick;
import com.almasb.fxgl.physics.CollisionDetectionStrategy;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxglgames.geowars.collision.BulletMineHandler;
import com.almasb.fxglgames.geowars.collision.PlayerCrystalHandler;
import com.almasb.fxglgames.geowars.component.GridComponent;
import com.almasb.fxglgames.geowars.component.PlayerComponent;
import com.almasb.fxglgames.geowars.menu.GeoWarsMainMenu;
import com.almasb.fxglgames.geowars.service.HighScoreService;
import com.almasb.fxglgames.geowars.service.PlayerPressureService;
import com.almasb.fxglgames.geowars.wave.WaveService;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

    private PlayerPressureService pressureService;

    private VirtualJoystick moveJoystick;
    private VirtualJoystick shootJoystick;

    public Entity getPlayer() {
        return player;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        var isRelease = IS_RELEASE;

        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("FXGL Space Wars");
        settings.setVersion("1.3.1");
        settings.setIntroEnabled(isRelease);
        settings.setMainMenuEnabled(IS_MENU);
        settings.setGameMenuEnabled(IS_MENU);
        settings.setFullScreenAllowed(isRelease);
        settings.setFullScreenFromStart(isRelease);
        settings.setProfilingEnabled(false);
        settings.setCollisionDetectionStrategy(CollisionDetectionStrategy.GRID_INDEXING);
        settings.setApplicationMode(isRelease ? ApplicationMode.RELEASE : ApplicationMode.DEVELOPER);
        settings.addEngineService(HighScoreService.class);
        settings.addEngineService(PlayerPressureService.class);
        settings.addEngineService(WaveService.class);
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

        getSettings().setGlobalSoundVolume(IS_SOUND_ENABLED ? 0.2 : 0.0);
        getSettings().setGlobalMusicVolume(IS_SOUND_ENABLED ? 0.5 : 0.0);

        loopBGM("bgm.mp3");
    }

    @Override
    protected void initInput() {

        if (!isOnMobile()) {
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

            // TODO: use sticks to aim?
            // TODO: allow virtual button + sticks
            getInput().addAction(new UserAction("Shoot Mouse") {
                @Override
                protected void onAction() {
                    playerComponent.shoot(getInput().getMousePositionWorld());
                }
            }, MouseButton.PRIMARY);
        }

        if (!isReleaseMode()) {
            onKeyDown(KeyCode.G, () -> {
                var e = spawn("Wanderer");
                GeoWarsFactory.respawnWanderer(e);
            });

            onKeyDown(KeyCode.H, () -> {
                byType(WANDERER).forEach(Entity::removeFromWorld);
            });

            onKeyDown(KeyCode.J, () -> {
                byType(GRID).forEach(e -> {
                    e.getComponent(GridComponent.class).applyDirectedForce(new Point2D(10000, 0), player.getCenter(), 1000);
                });
            });

            onKeyDown(KeyCode.T, () -> {
                getService(WaveService.class).spawnWave();
            });




            onKeyDown(KeyCode.Y, () -> {

                System.out.println(player.getPosition());

                var gameSubScene = new ChainExplosionSubScene();
                gameSubScene.getGameWorld().addEntityFactory(new GeoWarsFactory());

                gameSubScene.addTargets(player,
                        getGameWorld().getEntitiesFiltered(e -> e.isType(WANDERER))
                );

                getSceneService().pushSubScene(gameSubScene);
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

        pressureService = getService(PlayerPressureService.class);

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

            if (now >= GAME_OVER_SCORE)
                gameOver();
        });

        getWorldProperties().<Integer>addListener("lives", (prev, now) -> {
            if (now == 0)
                gameOver();
        });

        if (!IS_NO_ENEMIES) {
            initEnemySpawns();
        }
    }

    private void initEnemySpawns() {
        run(() -> {
            // spawn waves regardless of pressure level
            getService(WaveService.class).spawnWave();
        }, WAVE_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies() && geti("multiplier") >= 75) {
                spawn("Bouncer");
            }
        }, BOUNCER_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies() && geti("multiplier") >= 50) {
                spawn("Runner");
            }
        }, RUNNER_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies() && geti("multiplier") >= 25) {
                spawn("Seeker");
            }
        }, SEEKER_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies()) {
                for (int i = 0; i < 4; i++) {
                    var e = spawn("Wanderer");
                    GeoWarsFactory.respawnWanderer(e);
                }
            }
        }, WANDERER_SPAWN_INTERVAL);

        run(() -> {
            spawnFadeIn(
                    "Mine",
                    new SpawnData(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 80, getAppHeight() - 80))),
                    Duration.seconds(1)
            );
        }, MINE_SPAWN_INTERVAL);
    }

    private boolean isOnMobile() {
        return FXGL.isMobile();
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
                    killEnemy(enemy);
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, SEEKER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, RUNNER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOUNCER));
        physics.addCollisionHandler(bulletEnemy.copyFor(BULLET, BOMBER));
        physics.addCollisionHandler(new PlayerCrystalHandler());
        physics.addCollisionHandler(new BulletMineHandler());

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

                // remove all "removables"
                byType(WANDERER, SEEKER, RUNNER, BOUNCER, BOMBER, BULLET, CRYSTAL, MINE, SHOCKWAVE_PICKUP)
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
        physics.addCollisionHandler(playerEnemy.copyFor(PLAYER, MINE));

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

        var pressureText = getUIFactoryService().newText("", Color.WHITE, 24.0);
        pressureText.textProperty().bind(getService(PlayerPressureService.class).pressurePropProperty().asString("Pressure: %.2f"));

        if (!isReleaseMode()) {
            addUINode(pressureText, 60, 150);
        }

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

        if (isOnMobile()) {
            moveJoystick = getInput().createVirtualJoystick();
            shootJoystick = getInput().createVirtualJoystick();

            addUINode(moveJoystick, 40, getAppHeight() - 240);
            addUINode(shootJoystick, getAppWidth() - 240, getAppHeight() - 240);
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        if (isOnMobile()) {
            var moveVector = moveJoystick.getVector();
            var shootVector = shootJoystick.getVector();

            if (!moveVector.equals(Point2D.ZERO)) {
                player.translate(moveVector.multiply(playerComponent.getSpeed()));
            }

            if (!shootVector.equals(Point2D.ZERO)) {
                playerComponent.shootDirection(shootVector);
            }
        }
    }

    public void killEnemy(Entity enemy) {
        SpawnData data;

        if (enemy.isType(BOMBER)) {
            data = new SpawnData(enemy.getCenter()).put("numParticles", 10);
        } else {
            data = new SpawnData(enemy.getCenter()).put("numParticles", 200);
        }

        Entity explosion = spawn("Explosion", data);
        GeoWarsFactory.respawnExplosion(explosion, data);

        spawn("Crystal", enemy.getCenter());

        if (FXGLMath.randomBoolean(0.005)) {
            spawn("ShockwavePickup", enemy.getPosition());
        }

        addScoreKill(enemy.getCenter());

        enemy.removeFromWorld();
    }

    private void addScoreKill(Point2D enemyPosition) {
        inc("kills", +1);

        if (geti("kills") == 15) {
            set("kills", 0);
            inc("multiplier", +1);
        }

        final int multiplier = geti("multiplier");

        inc("score", +10*multiplier);

        Text bonusText = getUIFactoryService().newText("+10" + (multiplier == 1 ? "" : "x" + multiplier), Color.color(1, 1, 1, 0.8), 24);
        bonusText.setStroke(Color.GOLD);

        if (!FXGL.isMobile()) {
            bonusText.setEffect(new DropShadow(25, Color.WHITE));
        }
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
