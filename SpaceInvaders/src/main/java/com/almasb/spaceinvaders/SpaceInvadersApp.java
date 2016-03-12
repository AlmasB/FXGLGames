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
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.spaceinvaders.collision.BonusPlayerHandler;
import com.almasb.spaceinvaders.collision.BulletEnemyHandler;
import com.almasb.spaceinvaders.collision.BulletPlayerHandler;
import com.almasb.spaceinvaders.collision.BulletWallHandler;
import com.almasb.spaceinvaders.control.PlayerControl;
import com.almasb.spaceinvaders.event.BonusPickupEvent;
import com.almasb.spaceinvaders.event.GameEvent;
import com.almasb.spaceinvaders.tutorial.Tutorial;
import com.almasb.spaceinvaders.tutorial.TutorialStep;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.io.Serializable;
import java.util.stream.IntStream;

import static com.almasb.spaceinvaders.Config.*;

/**
 * A simple clone of Space Invaders. Demonstrates basic FXGL features.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Invaders");
        settings.setVersion("0.6");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initAchievements() {
        AchievementManager am = getAchievementManager();

        am.registerAchievement(new Achievement("Hitman", "Destroy " + ACHIEVEMENT_ENEMIES_KILLED + " enemies"));
        am.registerAchievement(new Achievement("Master Scorer", "Score " + ACHIEVEMENT_MASTER_SCORER + " points"));
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
        input.addInputMapping(new InputMapping("Shoot", MouseButton.PRIMARY));


    }

    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
    public void moveLeft() {
        playerControl.left();
    }

    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
    public void moveRight() {
        playerControl.right();
    }

    @OnUserAction(name = "Shoot", type = ActionType.ON_ACTION)
    public void shoot() {
        playerControl.shoot();
    }

    @Override
    protected void initAssets() {
        getAssetLoader().cache();
    }

    private GameEntity player;
    private PlayerControl playerControl;

    private IntegerProperty enemiesDestroyed;
    private IntegerProperty score;
    private IntegerProperty level;
    private IntegerProperty lives;

    private int highScore;
    private String highScoreName;

    private GameController uiController;

    @Override
    protected void preInit() {
        getAudioPlayer().setGlobalSoundVolume(0);
        getAudioPlayer().setGlobalMusicVolume(0);

        getNotificationService().setBackgroundColor(Color.DARKBLUE);

        getEventBus().addEventHandler(GameEvent.PLAYER_GOT_HIT, this::onPlayerGotHit);
        getEventBus().addEventHandler(GameEvent.ENEMY_KILLED, this::onEnemyKilled);
        getEventBus().addEventHandler(BonusPickupEvent.ANY, this::onBonusPickup);
    }

    @Override
    public void loadState(Serializable data) {
        SaveData saveData = (SaveData) data;

        initGame(saveData);
    }

    @Override
    protected void initGame() {
        initGame(highScore == 0
                ? new SaveData("CPU", ACHIEVEMENT_MASTER_SCORER)
                : new SaveData(highScoreName, highScore));
    }

    private void initGame(SaveData data) {
        //MainViewComponent.turnOnDebugBBox(Color.YELLOW);

        highScoreName = data.getName();
        highScore = data.getHighScore();

        enemiesDestroyed = new SimpleIntegerProperty();
        score = new SimpleIntegerProperty();
        level = new SimpleIntegerProperty();
        lives = new SimpleIntegerProperty(START_LIVES);

        getAchievementManager().getAchievementByName("Hitman")
                .bind(enemiesDestroyed.greaterThanOrEqualTo(ACHIEVEMENT_ENEMIES_KILLED));
        getAchievementManager().getAchievementByName("Master Scorer")
                .bind(score.greaterThanOrEqualTo(ACHIEVEMENT_MASTER_SCORER));

        spawnBackground();
        spawnPlayer();

        if (!runningFirstTime)
            nextLevel();
    }

    private void spawnBackground() {
        Entity bg = EntityFactory.newBackground(getWidth(), getHeight());

        getGameWorld().addEntity(bg);

        getMasterTimer().runAtInterval(() -> {
            Entity meteor = EntityFactory.newMeteor();

            getGameWorld().addEntity(meteor);
        }, Duration.seconds(3));
    }

    private void spawnEnemy(double x, double y) {
        Entity enemy = EntityFactory.newEnemy(x, y);

        getGameWorld().addEntity(enemy);
    }

    private void spawnPlayer() {
        player = EntityFactory.newPlayer(getWidth() / 2 - 20, getHeight() - 40);
        playerControl = player.getControlUnsafe(PlayerControl.class);

        getGameWorld().addEntity(player);
    }

    private void spawnWall(double x, double y) {
        getGameWorld().addEntity(EntityFactory.newWall(x, y));
    }

    private void spawnBonus(double x, double y, EntityFactory.BonusType type) {
        Entity bonus = EntityFactory.newBonus(x, y, type);

        getGameWorld().addEntity(bonus);
    }

    private void initLevel() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 8; x++) {
                spawnEnemy(x * (40 + 20), 100 + y * (40 + 20));
            }
        }

        spawnWall(40, getHeight() - 100);
        spawnWall(120, getHeight() - 100);

        spawnWall(getWidth() - 160, getHeight() - 100);
        spawnWall(getWidth() - 80, getHeight() - 100);

        getInput().setProcessInput(true);
    }

    private void cleanupLevel() {
        getGameWorld().getEntitiesByType(
                EntityFactory.EntityType.BULLET,
                EntityFactory.EntityType.BONUS,
                EntityFactory.EntityType.WALL)
                .forEach(Entity::removeFromWorld);
    }

    private void nextLevel() {
        getInput().setProcessInput(false);

        cleanupLevel();

        level.set(level.get() + 1);

        GameEntity levelInfo = new GameEntity();
        levelInfo.getPositionComponent().setValue(getWidth() / 2 - UIFactory.widthOf("Level " + level.get(), 44) / 2, 0);
        levelInfo.getMainViewComponent().setView(new EntityView(UIFactory.newText("Level " + level.get(), Color.AQUAMARINE, 44)), true);
        levelInfo.addControl(new ExpireCleanControl(Duration.seconds(LEVEL_START_DELAY)));

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
        ground.addControl(new ExpireCleanControl(Duration.seconds(LEVEL_START_DELAY)));
        ground.addComponent(new PhysicsComponent());

        getGameWorld().addEntities(levelInfo, ground);

        getMasterTimer().runOnceAfter(this::initLevel, Duration.seconds(LEVEL_START_DELAY));

        getAudioPlayer().playSound(Asset.SOUND_NEW_LEVEL);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();
        physicsWorld.addCollisionHandler(new BulletPlayerHandler());
        physicsWorld.addCollisionHandler(new BulletEnemyHandler());
        physicsWorld.addCollisionHandler(new BulletWallHandler());
        physicsWorld.addCollisionHandler(new BonusPlayerHandler());
    }

    @Override
    protected void initUI() {
        uiController = new GameController(getGameScene());

        Parent ui = getAssetLoader().loadFXML(Asset.FXML_MAIN_UI, uiController);

        uiController.getLabelScore().textProperty().bind(score.asString("Score: %d"));
        uiController.getLabelHighScore().setText("HiScore: " + highScore + " " + highScoreName + "");

        IntStream.range(0, lives.get())
                .forEach(i -> uiController.addLife());

        getGameScene().addUINode(ui);
    }

    private boolean runningFirstTime = false;

    @Override
    protected void onUpdate(double tpf) {
        if (runningFirstTime) {
            getDisplay().showConfirmationBox("Play Tutorial?", yes -> {
                if (yes)
                    playTutorial();
                else
                    nextLevel();
            });

            runningFirstTime = false;
        }
    }

    private void playTutorial() {
        getInput().setRegisterInput(false);

        // TODO: ideally we must obtain dynamic key codes because the keys
        // may have been reassigned
        TutorialStep step1 = new TutorialStep("Press A to move left", Asset.DIALOG_MOVE_LEFT, () -> {
            getInput().mockKeyPress(KeyCode.A);
        });

        TutorialStep step2 = new TutorialStep("Press D to move right", Asset.DIALOG_MOVE_RIGHT, () -> {
            getInput().mockKeyRelease(KeyCode.A);
            getInput().mockKeyPress(KeyCode.D);
        });

        TutorialStep step3 = new TutorialStep("Press F to shoot", Asset.DIALOG_SHOOT, () -> {
            getInput().mockKeyRelease(KeyCode.D);

            getInput().mockButtonPress(MouseButton.PRIMARY, 0, 0);
            getInput().mockButtonRelease(MouseButton.PRIMARY);
        });

        Text tutorialText = UIFactory.newText("", Color.AQUA, 24);
        tutorialText.textProperty().addListener((o, old, newText) -> {
            tutorialText.setTranslateX(getWidth() / 2 - UIFactory.widthOf(newText, 24) / 2);
        });

        tutorialText.setTranslateY(getHeight() / 2 - 50);
        getGameScene().addUINode(tutorialText);

        Tutorial tutorial = new Tutorial(tutorialText, () -> {
            player.getPositionComponent().setValue(getWidth() / 2 - 20, getHeight() - 40);

            getGameScene().removeUINode(tutorialText);
            nextLevel();

            getInput().setRegisterInput(true);
        }, step1, step2, step3);

        tutorial.play();
    }

    private void onPlayerGotHit(GameEvent event) {
        lives.set(lives.get() - 1);
        uiController.loseLife();

        playerControl.enableInvincibility();

        getMasterTimer().runOnceAfter(() -> {
            playerControl.disableInvincibility();
        }, Duration.seconds(INVINCIBILITY_TIME));

        getAudioPlayer().playSound(Asset.SOUND_LOSE_LIFE);

        if (lives.get() == 0)
            showGameOver();
    }

    private void onEnemyKilled(GameEvent event) {
        enemiesDestroyed.set(enemiesDestroyed.get() + 1);
        score.set(score.get() + SCORE_ENEMY_KILL * (getGameWorld().getGameDifficulty().ordinal() + SCORE_DIFFICULTY_MODIFIER));

        if (enemiesDestroyed.get() % 40 == 0)
            nextLevel();

        if (Math.random() < BONUS_SPAWN_CHANCE) {
            int bonusSize = EntityFactory.BonusType.values().length;

            spawnBonus(Math.random() * (getWidth() - 50), Math.random() * getHeight() / 3,
                    EntityFactory.BonusType.values()[(int)(Math.random()*bonusSize)]);
        }
    }

    private void onBonusPickup(BonusPickupEvent event) {
        switch (event.getType()) {
            case ATTACK_RATE:
                playerControl.increaseAttackSpeed(PLAYER_BONUS_ATTACK_SPEED);
                break;
            case LIFE:
                lives.set(lives.get() + 1);
                uiController.addLife();
                break;
        }
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
