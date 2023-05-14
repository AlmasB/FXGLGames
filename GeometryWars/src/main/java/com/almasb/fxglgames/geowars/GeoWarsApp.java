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
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.geowars.collision.BulletMineHandler;
import com.almasb.fxglgames.geowars.collision.PlayerPickupHandler;
import com.almasb.fxglgames.geowars.component.GridComponent;
import com.almasb.fxglgames.geowars.component.PlayerComponent;
import com.almasb.fxglgames.geowars.factory.PickupFactory;
import com.almasb.fxglgames.geowars.menu.GeoWarsMainMenu;
import com.almasb.fxglgames.geowars.service.HighScoreService;
import com.almasb.fxglgames.geowars.service.PlayerPressureService;
import com.almasb.fxglgames.geowars.wave.WaveService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.CacheHint;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
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
        settings.setVersion("1.4.0");
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

            getInput().addAction(new UserAction("Shoot Mouse Secondary") {
                @Override
                protected void onActionBegin() {
                    playerComponent.shootSecondary(getInput().getMousePositionWorld());
                }
            }, MouseButton.SECONDARY);
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

            onKeyDown(KeyCode.I, () -> {
                spawn("PickupRicochet", 400, 400);
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
        vars.put("secondaryCharge", 0);
        vars.put("weaponType", WeaponType.SINGLE);
        vars.put("hp", PLAYER_HP);
        vars.put("lastHitTime", 0);
        vars.put("time", 0.0);
    }

    @Override
    protected void initGame() {

        getGameWorld().addEntityFactory(new GeoWarsFactory());
        getGameWorld().addEntityFactory(new PickupFactory());

        getGameScene().setBackgroundColor(Color.color(0, 0, 0.05, 1.0));

        spawn("Background");
        spawn("ParticleLayer");

        player = spawn("Player");
        playerComponent = player.getComponent(PlayerComponent.class);
        playerComponent.playSpawnAnimation();

        pressureService = getService(PlayerPressureService.class);

        int dist = OUTSIDE_DISTANCE;

        getGameScene().getViewport().setLazy(true);
        getGameScene().getViewport().setBounds(-dist, -dist, getAppWidth() + dist, getAppHeight() + dist);
        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2.0 - player.getWidth() / 2, getAppHeight() / 2.0 - player.getHeight() / 2);

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

        getWorldProperties().<Integer>addListener("hp", (prev, now) -> {
            if (now > PLAYER_HP)
                set("hp", PLAYER_HP);

            if (now <= 0)
                killPlayer();
        });

        getWorldProperties().<Integer>addListener("secondaryCharge", (prev, now) -> {
            if (now > MAX_CHARGES_SECONDARY)
                set("secondaryCharge", MAX_CHARGES_SECONDARY);
        });

        run(() -> inc("hp", TIME_PENALTY), PENALTY_INTERVAL);
        run(() -> inc("time", +1.0), Duration.seconds(1));

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
            if (pressureService.isSpawningEnemies() && getd("time") >= 40) {
                spawn("Bouncer");
            }
        }, BOUNCER_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies() && getd("time") >= 20) {
                spawn("Runner");
            }
        }, RUNNER_SPAWN_INTERVAL);

        run(() -> {
            if (pressureService.isSpawningEnemies()) {
                var numToSpawn = geti("multiplier") >= 100 ? 4 : 2;

                for (int i = 0; i < numToSpawn; i++) {
                    spawn("Seeker");
                }
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

        run(() -> {
            spawnFadeIn(
                    "PickupRicochet",
                    new SpawnData(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 80, getAppHeight() - 80))),
                    Duration.seconds(1)
            );
        }, PICKUP_RICOCHET_SPAWN_INTERVAL);
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

        var pickupHandler = new PlayerPickupHandler();

        physics.addCollisionHandler(pickupHandler);
        physics.addCollisionHandler(pickupHandler.copyFor(PLAYER, PICKUP_RICOCHET));
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
                if (System.nanoTime() > geti("lastHitTime") + 100000000) {
                    set("lastHitTime", (int)System.nanoTime());

                    inc("hp", COLLISION_PENALTY);

                    killEnemy(b);
                }
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

    private void killPlayer() {
        // remove all "removables"
        byType(WANDERER, SEEKER, RUNNER, BOUNCER, BOMBER, BULLET, PICKUP_CRYSTAL, MINE, SHOCKWAVE_PICKUP)
                .forEach(Entity::removeFromWorld);

        player.setPosition(getAppWidth() / 2, getAppHeight() / 2);
        playerComponent.playSpawnAnimation();

        inc("lives", -1);
        set("kills", 0);
        set("secondaryCharge", 0);
        set("multiplier", 1);
        set("hp", PLAYER_HP);
        set("time", 0.0);
        set("weaponType", WeaponType.SINGLE);
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

        var pressureText = getUIFactoryService().newText("", Color.WHITE, 24.0);
        pressureText.textProperty().bind(getService(PlayerPressureService.class).pressurePropProperty().asString("Pressure: %.2f"));

        if (!isReleaseMode()) {
            addUINode(pressureText, 60, 150);
        }

        ProgressBar hpBar = new ProgressBar(false);
        hpBar.setFill(Color.GREEN.brighter());
        hpBar.setTraceFill(Color.GREEN.brighter());
        hpBar.setMaxValue(PLAYER_HP);
        hpBar.setLabelVisible(false);
        hpBar.currentValueProperty().bind(getip("hp"));
        hpBar.setTranslateX(getAppWidth() / 2.0 - 200 / 2.0);
        hpBar.setTranslateY(60);

        ProgressBar secondaryBar = new ProgressBar(false);
        secondaryBar.setFill(Color.LIGHTBLUE.brighter().brighter());
        secondaryBar.setTraceFill(Color.LIGHTBLUE.brighter());
        secondaryBar.setMaxValue(MAX_CHARGES_SECONDARY);
        secondaryBar.setLabelVisible(false);
        secondaryBar.opacityProperty().bind(
                Bindings.when(getip("secondaryCharge").lessThan(MAX_CHARGES_SECONDARY))
                        .then(0.45)
                        .otherwise(1.0)
        );
        secondaryBar.currentValueProperty().bind(getip("secondaryCharge"));
        secondaryBar.setTranslateX(getAppWidth() / 2.0 - 200 / 2.0);
        secondaryBar.setTranslateY(75);

        var ricochetText = getUIFactoryService().newText("RICOCHET", Color.ANTIQUEWHITE, 12.0);
        ricochetText.setTranslateX(hpBar.getTranslateX());
        ricochetText.setTranslateY(hpBar.getTranslateY() + 57);
        ricochetText.visibleProperty().bind(getbp("isRicochet"));

        var centerLine = new Line(getAppWidth() / 2.0, 0, getAppWidth() / 2.0, getAppHeight());
        centerLine.setStroke(Color.RED);

        getGameScene().addUINodes(multText, scoreText, livesText, ricochetText, hpBar, secondaryBar);

        Text goodLuck = getUIFactoryService().newText("Kill enemies to survive!", Color.AQUA, 38);

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

        inc("hp", +1);
        inc("secondaryCharge", +1);

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
