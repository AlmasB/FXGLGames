package com.almasb.fxglgames.mario;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxglgames.mario.collisions.PlayerCoinHandler;
import com.almasb.fxglgames.mario.collisions.PlayerPortalHandler;
import com.almasb.fxglgames.mario.components.*;
import com.almasb.fxglgames.mario.ui.HealthIndicator;
import com.almasb.fxglgames.mario.ui.LevelEndScene;
import com.almasb.fxglgames.mario.ui.MarioLoadingScene;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.mario.MarioType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    private static final int MAX_LEVEL = 21;
    private static final int STARTING_LEVEL = 0;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public LoadingScene newLoadingScene() {
                return new MarioLoadingScene();
            }
        });
        settings.setApplicationMode(ApplicationMode.RELEASE);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).left();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(PlayerComponent.class).right();
            }

            @Override
            protected void onActionEnd() {
                player.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(PlayerComponent.class).jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                getGameWorld().getEntitiesByType(BUTTON)
                        .stream()
                        .filter(btn -> btn.hasComponent(CollidableComponent.class) && player.isColliding(btn))
                        .forEach(btn -> {
                            btn.removeComponent(CollidableComponent.class);

                            // TODO: easy access of set views
                            //KeyView view = btn.getViewComponent().getChildView() ? or may getMainView also?;

                            Entity keyEntity = btn.getObject("keyEntity");
                            keyEntity.setProperty("activated", true);

                            KeyView view = (KeyView) ((EntityView) keyEntity.getView()).getNodes().get(0);
                            view.setKeyColor(Color.RED);

                            makeExitDoor();
                        });
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Catapult") {
            @Override
            protected void onActionBegin() {
                if (getb("canCatapult")) {
                    Point2D vector = getInput().getVectorToMouse(player.getPosition());

                    player.getComponent(PlayerComponent.class).superJump(vector.normalize().multiply(900));
                }
            }
        }, MouseButton.PRIMARY);

        if (!isRelease()) {
            DeveloperActions.add(getInput());
        }
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", STARTING_LEVEL);
        vars.put("levelTime", 0.0);
        vars.put("score", 0);
        vars.put("canCatapult", false);
    }

    private boolean firstTime = true;
    private LevelEndScene levelEndScene;

    @Override
    protected void onPreInit() {
        if (isRelease()) {
            getSettings().setGlobalMusicVolume(0.25);
            loopBGM("BGM_dash_runner.wav");
        }
    }

    private boolean isRelease() {
        return getSettings().getApplicationMode() == ApplicationMode.RELEASE;
    }

    @Override
    protected void initGame() {
        if (firstTime) {
            levelEndScene = new LevelEndScene();
            firstTime = false;
        }

        getGameWorld().addEntityFactory(new MarioFactory());

        player = null;
        nextLevel();

        // player must be spawned after call to nextLevel, otherwise player gets removed
        // before the update tick _actually_ adds the player to game world
        player = getGameWorld().spawn("player", 50, 50);

        var catapultLineIndicator = getGameWorld().spawn("catapultLineIndicator");
        catapultLineIndicator.getViewComponent().opacityProperty().bind(
                Bindings.when(getbp("canCatapult")).then(1.0).otherwise(0.0)
        );
        catapultLineIndicator.xProperty().bind(player.xProperty().add(16));
        catapultLineIndicator.yProperty().bind(player.yProperty().add(21));

        set("player", player);

        if (isRelease()) {
            for (int i = 10; i >= 0; i--) {
                spawn("background", new SpawnData(0, 0).put("index", i));
            }
        }

        Viewport viewport = getGameScene().getViewport();

        viewport.setBounds(-1500, 0, 250 * 70, getAppHeight());
        viewport.bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);

        viewport.setLazy(true);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 760);
        getPhysicsWorld().addCollisionHandler(new PlayerCoinHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerPortalHandler());

        onCollisionBegin(PLAYER, DOOR_BOT, (player, door) -> {
            door.removeComponent(CollidableComponent.class);

            levelEndScene.onLevelFinish();

            // the above runs in its own scene, so fade will wait until
            // the user exits that scene
            getGameScene().getViewport().fade(() -> {
                nextLevel();
            });
        });

        onCollisionBegin(PLAYER, EXIT_SIGN, (player, sign) -> {
            sign.removeComponent(CollidableComponent.class);

            var texture = texture("exit_sign.png").brighter();
            texture.setTranslateX(sign.getX() + 9);
            texture.setTranslateY(sign.getY() + 13);

            var gameView = new GameView(texture, 150);

            getGameScene().addGameView(gameView);

            runOnce(() -> getGameScene().removeGameView(gameView), Duration.seconds(1.6));
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, BUTTON) {
            @Override
            protected void onCollisionBegin(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");

                if (!keyEntity.isActive()) {
                    keyEntity.setProperty("activated", false);
                    getGameWorld().addEntity(keyEntity);
                }

                keyEntity.getViewComponent().setOpacity(1);
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity btn) {
                Entity keyEntity = btn.getObject("keyEntity");
                if (!keyEntity.getBoolean("activated")) {
                    keyEntity.getViewComponent().setOpacity(0);
                }
            }
        });

        onCollisionBegin(PLAYER, EXIT_TRIGGER, (player, trigger) -> {
            trigger.removeComponent(CollidableComponent.class);

            makeExitDoor();
        });

        onCollisionBegin(PLAYER, ENEMY, (player, enemy) -> {
            player.getComponent(PlayerComponent.class).onHit(enemy);
        });

        onCollisionBegin(PLAYER, MESSAGE_PROMPT, (player, prompt) -> {
            // TODO: can we add something like SingleCollidable?
            prompt.removeComponent(CollidableComponent.class);
            prompt.getViewComponent().setOpacity(1);

            runOnce(() -> prompt.removeFromWorld(), Duration.seconds(4.5));
        });

        onCollisionBegin(PLAYER, QUESTION, (player, question) -> {
            var q = question.getString("question");
            var a = question.getString("answer");

            getDisplay().showInputBox("Question: " + q + "?", answer -> {
                if (a.equals(answer)) {
                    question.removeFromWorld();
                }
            });
        });

        onCollisionBegin(PLAYER, TIMEOUT_BOX, (player, box) -> {
            box.getComponent(CollidableComponent.class).setValue(false);

            box.getComponent(TimeoutBoxComponent.class).startCountdown();
        });

        onCollisionBegin(PLAYER, LOOT_BOX, (player, box) -> {
            box.getComponent(CollidableComponent.class).setValue(false);

            box.getComponent(LootBoxComponent.class).open();
        });

        onCollisionBegin(PLAYER, JUMP_PAD, (player, pad) -> {
            player.getComponent(PlayerComponent.class).superJump();

            pad.getComponent(JumpPadComponent.class).activate();
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PLAYER, CATAPULT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity catapult) {
                set("canCatapult", true);
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity catapult) {
                set("canCatapult", false);
            }
        });

        onCollisionBegin(PLAYER, KEY_PROMPT, (player, prompt) -> {
            String key = prompt.getString("key");

            var entity = getGameWorld().create("keyCode", new SpawnData(prompt.getX(), prompt.getY()).put("key", key));
            spawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_OUT());

            runOnce(() -> {
                despawnWithScale(entity, Duration.seconds(1), Interpolators.ELASTIC.EASE_IN());
            }, Duration.seconds(2.5));
        });
    }

    private void makeExitDoor() {
        var doorTop = getGameWorld().getSingleton(DOOR_TOP);
        var doorBot = getGameWorld().getSingleton(DOOR_BOT);

        doorBot.getComponent(CollidableComponent.class).setValue(true);

        doorTop.getViewComponent().setOpacity(1);
        doorBot.getViewComponent().setOpacity(1);
    }

    private void nextLevel() {
        if (geti("level") == MAX_LEVEL) {
            getDisplay().showMessageBox("You finished the game!");
            return;
        }

        // we play test the same level if dev mode
        // so only increase level if release mode
        if (isRelease()) {
            inc("level", +1);
        }

        setLevel(geti("level"));
    }

    @Override
    protected void initUI() {
        var hp = new HealthIndicator(player.getComponent(HPComponent.class));

        var coin = texture("ui/coin.png", 48 * 0.75, 51 * 0.75);

        var scoreText = getUIFactory().newText("", Color.GOLD, 38.0);
        scoreText.setStrokeWidth(2.5);
        scoreText.setStroke(Color.color(0.0, 0.0, 0.0, 0.56));
        scoreText.textProperty().bind(getip("score").asString());

        addUINode(hp);
        addUINode(coin, 130, 15);
        addUINode(scoreText, 170, 48);

        // TODO: add convenience methods to map game world coord to UI coord
        //var line = new Line();
        //line.startXProperty().bind(player.xProperty());
    }

    @Override
    protected void onUpdate(double tpf) {
        inc("levelTime", tpf);

        if (player.getY() > getAppHeight()) {
            onPlayerDied();
        }
    }

    public void onPlayerDied() {
        setLevel(geti("level"));
    }

    private void setLevel(int levelNum) {
        if (player != null) {
            player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(50, 50));
            player.setZ(Integer.MAX_VALUE);

            player.getComponent(PlayerComponent.class).restoreHP();
        }

        set("levelTime", 0.0);

        var levelFile = new File("level0.tmx");

        Level level;

        // this supports hot reloading of levels during development
        if (!isRelease() && levelFile.exists()) {
            System.out.println("Loading from development level");

            try {
                level = new TMXLevelLoader().load(levelFile.toURI().toURL(), getGameWorld());
                getGameWorld().setLevel(level);

                System.out.println("Success");

            } catch (Exception e) {
                level = setLevelFromMap("tmx/level" + levelNum  + ".tmx");
            }
        } else {
            level = setLevelFromMap("tmx/level" + levelNum  + ".tmx");
        }

        var shortestTime = level.getProperties().getDouble("star1time");

        var levelTimeData = new LevelEndScene.LevelTimeData(shortestTime * 2.4, shortestTime*1.3, shortestTime);

        set("levelTimeData", levelTimeData);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
