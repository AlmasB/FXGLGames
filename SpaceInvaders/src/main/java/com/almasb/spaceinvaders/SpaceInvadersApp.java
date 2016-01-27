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

package com.almasb.spaceinvaders;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.spaceinvaders.collision.BulletEnemyHandler;
import com.almasb.spaceinvaders.collision.BulletPlayerHandler;
import com.almasb.spaceinvaders.component.InvincibleComponent;
import com.almasb.spaceinvaders.component.OwnerComponent;
import com.almasb.spaceinvaders.event.GameEvent;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.io.Serializable;

import static com.almasb.spaceinvaders.EntityFactory.EntityType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    private static final String SAVE_DATA_NAME = "hiscore.dat";

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Invaders");
        settings.setVersion("0.5");
        settings.setWidth(650);
        settings.setHeight(800);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initAchievements() {
        AchievementManager am = getAchievementManager();

        am.registerAchievement(new Achievement("Hitman", "Destroy 5 enemies"));
        am.registerAchievement(new Achievement("Master Scorer", "Score 10000+ points"));
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
        input.addInputMapping(new InputMapping("Shoot", KeyCode.F));
    }

    @Override
    protected void initAssets() {
        EntityFactory.preLoad();
    }

    private GameEntity player;
    private IntegerProperty enemiesDestroyed;
    private IntegerProperty score;
    private IntegerProperty level;
    private IntegerProperty lives;

    private int highScore;
    private String highScoreName;

    @Override
    protected void preInit() {
        getAudioPlayer().setGlobalSoundVolume(0);
        getAudioPlayer().setGlobalMusicVolume(0);

        getNotificationService().setBackgroundColor(Color.DARKBLUE);

        //IOResult<SaveData> io = getSaveLoadManager().load(SAVE_DATA_NAME);

        //SaveData data = io.hasData() ? io.getData() : new SaveData("CPU", 10000);

        SaveData data = new SaveData("CPU", 10000);

        highScoreName = data.getName();
        highScore = data.getHighScore();

        getEventBus().addEventHandler(GameEvent.PLAYER_GOT_HIT, event -> {
            lives.set(lives.get() - 1);
            if (lives.get() == 0)
                showGameOver();
        });

        getEventBus().addEventHandler(GameEvent.ENEMY_KILLED, event -> {
            enemiesDestroyed.set(enemiesDestroyed.get() + 1);
            score.set(score.get() + 200);

            if (enemiesDestroyed.get() % 40 == 0)
                nextLevel();
        });
    }

    // workaround until saves are properly recognized
    @Override
    public void loadState(Serializable data) {
        initGame();
    }

    @Override
    protected void initGame() {
        initBackground();

        enemiesDestroyed = new SimpleIntegerProperty(0);
        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);

        getAchievementManager().getAchievementByName("Hitman")
                .bind(enemiesDestroyed.greaterThanOrEqualTo(5));
        getAchievementManager().getAchievementByName("Master Scorer")
                .bind(score.greaterThanOrEqualTo(10000));

        spawnPlayer();

        if (!runningFirstTime)
            nextLevel();
    }

    private void initBackground() {
        Entity bg = EntityFactory.newBackground(getWidth(), getHeight());

        getGameWorld().addEntity(bg);
    }

    private void initLevel() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 8; x++) {
                spawnEnemy(x * (40 + 20), 100 + y * (40 + 20));
            }
        }

        getInput().setProcessActions(true);
    }

    private void nextLevel() {
        getInput().setProcessActions(false);
        level.set(level.get() + 1);

        GameEntity levelInfo = new GameEntity();
        levelInfo.getPositionComponent().setValue(getWidth() / 2 - UIFactory.widthOf("Level " + level.get(), 44) / 2, 0);
        levelInfo.getMainViewComponent().setView(new EntityView(UIFactory.newText("Level " + level.get(), Color.AQUAMARINE, 44)), true);
        levelInfo.addControl(new ExpireCleanControl(Duration.seconds(2.4)));

        PhysicsComponent pComponent = new PhysicsComponent();
        pComponent.setBodyType(BodyType.DYNAMIC);
        pComponent.setOnPhysicsInitialized(() -> pComponent.setLinearVelocity(0, 5));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setDensity(0.05f);
        fixtureDef.setRestitution(0.3f);

        pComponent.setFixtureDef(fixtureDef);
        levelInfo.addComponent(pComponent);

        GameEntity ground = new GameEntity();
        ground.getPositionComponent().setY(getHeight() / 2);
        ground.getMainViewComponent().setView(new EntityView(new Rectangle(getWidth(), 100, Color.TRANSPARENT)), true);
        ground.addControl(new ExpireCleanControl(Duration.seconds(2.4)));
        ground.addComponent(new PhysicsComponent());

        getGameWorld().addEntities(levelInfo, ground);

        getMasterTimer().runOnceAfter(this::initLevel, Duration.seconds(2.4));

        getAudioPlayer().playSound("level.wav");
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();
        physicsWorld.addCollisionHandler(new BulletPlayerHandler());
        physicsWorld.addCollisionHandler(new BulletEnemyHandler());

//        physicsWorld.addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.PLAYER) {
//            @Override
//            protected void onCollisionBegin(Entity bullet, Entity player) {
//                Object owner = bullet.getComponentUnsafe(OwnerComponent.class).getValue();
//
//                // player shot that bullet so no need to handle collision
//                if (owner == EntityType.PLAYER || player.getComponentUnsafe(InvincibleComponent.class).getValue()) {
//                    return;
//                }
//
//                bullet.removeFromWorld();
//                lives.set(lives.get() - 1);
//                if (lives.get() == 0)
//                    showGameOver();
//            }
//        });

//        physicsWorld.addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ENEMY) {
//            @Override
//            protected void onCollisionBegin(Entity bullet, Entity enemy) {
//                Object owner = bullet.getComponentUnsafe(OwnerComponent.class).getValue();
//
//                // some enemy shot the bullet, skip collision handling
//                if (owner == EntityType.ENEMY) {
//                    return;
//                }
//
//                bullet.removeFromWorld();
//                enemy.removeFromWorld();
//                enemiesDestroyed.set(enemiesDestroyed.get() + 1);
//                score.set(score.get() + 200);
//
//                Entity explosion = EntityFactory.newExplosion(Entities.getBBox(enemy).getCenterWorld());
//                getGameWorld().addEntity(explosion);
//
//                getAudioPlayer().playSound("explosion.wav");
//
//                if (enemiesDestroyed.get() % 40 == 0)
//                    nextLevel();
//            }
//        });
    }

    @Override
    protected void initUI() {
        Text textScore = UIFactory.newText("", Color.AQUAMARINE, 18);
        textScore.textProperty().bind(score.asString("Score:[%d]"));

        Text textHighScore = UIFactory.newText("", Color.AQUAMARINE, 18);
        textHighScore.setText("HiScore:[" + highScore + "](" + highScoreName + ")");

        for (int i = 0; i < lives.get(); i++) {
            final int index = i;
            Texture t = getAssetLoader().loadTexture("life.png");
            t.setFitWidth(16);
            t.setFitHeight(16);
            t.setTranslateX(getWidth() * 4 / 5 + i * 32);
            t.setTranslateY(10);

            lives.addListener((observable, oldValue, newValue) -> {
                // if lives get reduced, the index gives us which life
                if (newValue.intValue() == index) {
                    Animation animation = getAnimationLoseLife(t);
                    animation.setOnFinished(e -> getGameScene().removeUINode(t));
                    animation.play();

                    GameEntity flash = new GameEntity();
                    flash.getMainViewComponent().setGraphics(new Rectangle(getWidth(), getHeight(), Color.rgb(190, 10, 15, 0.5)));

                    getGameWorld().addEntity(flash);

                    player.getComponentUnsafe(InvincibleComponent.class).setValue(true);

                    getMasterTimer().runOnceAfter(() -> {
                        flash.removeFromWorld();
                        player.getComponentUnsafe(InvincibleComponent.class).setValue(false);
                    }, Duration.seconds(1));

                    getAudioPlayer().playSound("lose_life.wav");
                }
            });

            getGameScene().addUINode(t);
        }

        HBox textBox = new HBox(20, textScore, textHighScore);
        textBox.setTranslateX(25);
        textBox.setTranslateY(10);

        getGameScene().addUINodes(textBox);
    }

    private boolean runningFirstTime = true;

    @Override
    protected void onUpdate() {
        if (runningFirstTime) {
            getDisplay().showConfirmationBox("Play Tutorial?", yes -> {
                if (yes)
                    playTutorial();
                else
                    nextLevel();
            });

            runningFirstTime = false;
        }

        if (mockLeft)
            moveLeft();

        if (mockRight)
            moveRight();
    }

    // workaround until FXGL can do it
    private boolean mockLeft, mockRight;

    private void playTutorial() {
        getInput().setProcessActions(false);

        Text hint = UIFactory.newText("Press A to move left", Color.AQUA, 24);
        hint.setTranslateX(getWidth() / 2 - UIFactory.widthOf(hint.getText(), 24) / 2);
        hint.setTranslateY(getHeight() / 2 - 50);
        getGameScene().addUINode(hint);

        mockLeft = true;
        getAudioPlayer().playMusic("dialogs/move_left.mp3");

        getMasterTimer().runOnceAfter(() -> {
            hint.setText("Press D to move right");
            hint.setTranslateX(getWidth() / 2 - UIFactory.widthOf(hint.getText(), 24) / 2);

            mockLeft = false;
            mockRight = true;

            getAudioPlayer().playMusic("dialogs/move_right.mp3");

            getMasterTimer().runOnceAfter(() -> {
                hint.setText("Press F to shoot");
                hint.setTranslateX(getWidth() / 2 - UIFactory.widthOf(hint.getText(), 24) / 2);

                mockRight = false;
                shoot();

                getAudioPlayer().playMusic("dialogs/shoot.mp3");

                getMasterTimer().runOnceAfter(() -> {
                    player.getPositionComponent().setValue(getWidth() / 2 - 20, getHeight() - 40);

                    getGameScene().removeUINode(hint);
                    nextLevel();
                }, Duration.seconds(3));
            }, Duration.seconds(3));
        }, Duration.seconds(3));
    }

    private Animation getAnimationLoseLife(Texture texture) {
        texture.setFitWidth(64);
        texture.setFitHeight(64);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.66), texture);
        tt.setToX(getWidth() / 2 - texture.getFitWidth() / 2);
        tt.setToY(getHeight() / 2 - texture.getFitHeight() / 2);

        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), texture);
        st.setToX(0);
        st.setToY(0);

        return new SequentialTransition(tt, st);
    }

    private void spawnEnemy(double x, double y) {
        Entity enemy = EntityFactory.newEnemy(x, y);

        getGameWorld().addEntity(enemy);
    }

    private void spawnPlayer() {
        player = EntityFactory.newPlayer(getWidth() / 2 - 20, getHeight() - 40);

        getGameWorld().addEntity(player);
    }

    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
    public void moveLeft() {
        if (player.getPositionComponent().getX() >= 5)
            player.getPositionComponent().translateX(-5);
    }

    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
    public void moveRight() {
        if (player.getPositionComponent().getX() <= getWidth() - player.getBoundingBoxComponent().getWidth() - 5)
            player.getPositionComponent().translate(5, 0);
    }

    @OnUserAction(name = "Shoot", type = ActionType.ON_ACTION_BEGIN)
    public void shoot() {
        Entity bullet = EntityFactory.newBullet(player);

        getGameWorld().addEntity(bullet);

        getAudioPlayer().playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    private void showGameOver() {
        getDisplay().showConfirmationBox("Game Over. Continue?", yes -> {
            if (yes) {
                startNewGame();
            } else {
                if (score.get() > highScore) {
                    getDisplay().showInputBox("Enter your name", playerName -> {
                        getSaveLoadManager().save(new SaveData(playerName, score.get()), SAVE_DATA_NAME);
                        exit();
                    });
                } else {
                    exit();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
