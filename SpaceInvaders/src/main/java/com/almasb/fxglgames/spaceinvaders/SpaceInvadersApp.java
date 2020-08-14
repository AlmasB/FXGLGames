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

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxglgames.spaceinvaders.collision.*;
import com.almasb.fxglgames.spaceinvaders.components.EnemyComponent;
import com.almasb.fxglgames.spaceinvaders.components.PlayerComponent;
import com.almasb.fxglgames.spaceinvaders.event.BonusPickupEvent;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import com.almasb.fxglgames.spaceinvaders.level.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.spaceinvaders.Config.*;

/**
 * A modernized clone of Space Invaders.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Invaders");
        settings.setVersion("1.0.1");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setProfilingEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setIntroEnabled(false);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.getCredits().addAll(Arrays.asList(
                "Music by Eric Matyas",
                "www.soundimage.org"
        ));
        settings.setApplicationMode(ApplicationMode.RELEASE);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        onKey(KeyCode.A, "Move Left", () -> playerComponent.left());
        onKey(KeyCode.D, "Move Right", () -> playerComponent.right());
        onKeyDown(KeyCode.F, "Red Laser", () -> playerComponent.shootRedLaser());
        onBtn(MouseButton.PRIMARY, "Shoot", () -> playerComponent.shoot());
        onBtn(MouseButton.SECONDARY, "Laser Beam", () -> playerComponent.shootLaser());

        // developer cheats
        if (getSettings().getApplicationMode() != ApplicationMode.RELEASE) {
            input.addAction(new UserAction("Next Level") {
                @Override
                protected void onActionBegin() {
                    nextLevel();
                }
            }, KeyCode.L);
        }
    }

    private Entity player;
    private PlayerComponent playerComponent;

    private int highScore;
    private String highScoreName;

    private SpaceInvadersController uiController;

    private List<SpaceLevel> levels;

    @Override
    protected void onPreInit() {
        getSettings().setGlobalSoundVolume(0.1);
        getSettings().setGlobalMusicVolume(1.0);

        loopBGM("bgm.mp3");

        onEvent(BonusPickupEvent.ANY, this::onBonusPickup);
        onEvent(GameEvent.ENEMY_KILLED, this::onEnemyKilled);
        onEvent(GameEvent.ENEMY_REACHED_END, this::onEnemyReachedEnd);
        onEvent(GameEvent.PLAYER_GOT_HIT, this::onPlayerGotHit);
    }

    private SaveData savedData = null;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("level", 0);
        vars.put("lives", START_LIVES);
        vars.put("enemiesKilled", 0);
        vars.put("laserMeter", 0.0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new SpaceInvadersFactory());

        // we have to use file system directly, since we are running without menus
        getFileSystemService().<SaveData>readDataTask(SAVE_DATA_NAME)
                .onSuccess(data -> savedData = data)
                .onFailure(ignore -> {})
                .run();

        initGame(savedData == null
                ? new SaveData("CPU", ACHIEVEMENT_MASTER_SCORER)
                : savedData);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BonusPlayerHandler());
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
        getPhysicsWorld().addCollisionHandler(new BulletPlayerHandler());
        getPhysicsWorld().addCollisionHandler(new BulletWallHandler());
        getPhysicsWorld().addCollisionHandler(new LaserBeamEnemyHandler());
    }

    private void initGame(SaveData data) {
        highScoreName = data.getName();
        highScore = data.getHighScore();

        levels = Arrays.asList(
                new Level20(),
                new Level1(),
                new Level2(),
                new Level3(),
                new Level4(),
                new Level5(),
                new Level6(),
                new Level7(),
                new Level8(),
                new Level9(),
                new Level10(),
                new Level11(),
                new Level12(),
                new Level13(),
                new Level14(),
                new Level15(),
                new Level16(),
                new Level17(),
                new Level18(),
                new Level19()
        );

        spawnBackground();
        spawnPlayer();

        if (!runningFirstTime)
            nextLevel();
    }

    private void spawnBackground() {
        spawn("Background");

        spawn("Stars");
        
        run(() -> spawn("Meteor"), Duration.seconds(3));
    }

    private void spawnPlayer() {
        player = spawn("Player", getAppWidth() / 2 - 20, getAppHeight() - 40);
        playerComponent = player.getComponent(PlayerComponent.class);
    }

    private void spawnWall(double x, double y) {
        spawn("Wall", x, y);
    }

    private void spawnRandomBonus() {
        spawnBonus(FXGLMath.random(BonusType.values()).get());
    }

    private void spawnBonus(BonusType type) {
        double x = FXGLMath.random(0, getAppWidth() - 50);
        double y = FXGLMath.random(0, getAppHeight() / 3);

        getGameWorld().spawn("Bonus", new SpawnData(x, y).put("type", type));
    }

    private void nextLevel() {
        getInput().setProcessInput(false);

        if (geti("level") > 0) {
            cleanupLevel();
        }

        set("enemiesKilled", 0);
        inc("level", +1);

        if (geti("level") > levels.size()) {
            showGameOver();
            return;
        }

        playInCutscene(() -> {
            spawn("LevelInfo");

            runOnce(this::initLevel, Duration.seconds(LEVEL_START_DELAY));

            play(Asset.SOUND_NEW_LEVEL);
        });
    }

    private void initLevel() {
        getCurrentLevel().init();

        // TODO: move wall init to level so we can have walls in different places
        spawnWall(40, getAppHeight() - 100);
        spawnWall(120, getAppHeight() - 100);

        spawnWall(getAppWidth() - 160 - 40, getAppHeight() - 100);
        spawnWall(getAppWidth() - 80 - 40, getAppHeight() - 100);

        getInput().setProcessInput(true);
    }

    private void cleanupLevel() {
        getGameWorld().getEntitiesByType(
                SpaceInvadersType.ENEMY,
                SpaceInvadersType.BONUS,
                SpaceInvadersType.WALL,
                SpaceInvadersType.BULLET,
                SpaceInvadersType.LASER_BEAM)
                .forEach(Entity::removeFromWorld);

        getCurrentLevel().destroy();
    }

    private void playInCutscene(Runnable onFinished) {
        getCurrentLevel().playInCutscene(onFinished);
    }

    private void playOutCutscene(Runnable onFinished) {
        getCurrentLevel().playOutCutscene(onFinished);
    }

    private SpaceLevel getCurrentLevel() {
        // levels are counted 1, 2, 3 ...
        // list indices are   0, 1, 2 ...
        return levels.get(geti("level") - 1);
    }

    @Override
    protected void initUI() {
        uiController = new SpaceInvadersController(getGameScene());

        UI ui = getAssetLoader().loadUI(Asset.FXML_MAIN_UI, uiController);

        uiController.getLabelScore().textProperty().bind(getip("score").asString("Score: %d"));
        uiController.getLabelHighScore().setText("HiScore: " + highScore + " " + highScoreName + "");
        uiController.getLaserMeter().currentValueProperty().bind(getdp("laserMeter"));

        IntStream.range(0, geti("lives"))
                .forEach(i -> uiController.addLife());

        getGameScene().addUI(ui);
    }

    private boolean runningFirstTime = true;

    @Override
    protected void onUpdate(double tpf) {
        if (runningFirstTime) {
            nextLevel();
            getNotificationService().pushNotification("Music: Eric Matyas www.soundimage.org");
            runningFirstTime = false;
        }

        if (geti("level") > levels.size()) {
            showGameOver();
            return;
        }

        getCurrentLevel().onUpdate(tpf);
    }

    public void onPlayerGotHit(GameEvent event) {
        getGameScene().getViewport().shakeTranslational(9.5);

        inc("lives", -1);
        uiController.loseLife();

        playerComponent.enableInvincibility();

        runOnce(playerComponent::disableInvincibility, Duration.seconds(INVINCIBILITY_TIME));

        play(Asset.SOUND_LOSE_LIFE);

        if (geti("lives") == 0)
            showGameOver();
    }

    private int scoreForKill() {
        return SCORE_ENEMY_KILL * (getSettings().getGameDifficulty().ordinal() + SCORE_DIFFICULTY_MODIFIER);
    }

    public void onEnemyKilled(GameEvent event) {
        inc("enemiesKilled", +1);
        inc("score", scoreForKill());

        if (!playerComponent.isLaserBeamActive() && getd("laserMeter") < LASER_METER_MAX) {
            inc("laserMeter", +LASER_METER_RECHARGE);
            if (getd("laserMeter") > LASER_METER_MAX) {
                set("laserMeter", LASER_METER_MAX);
            }
        }

        if (FXGLMath.randomBoolean(BONUS_SPAWN_CHANCE)) {
            spawnRandomBonus();
        }

        if (getCurrentLevel().isFinished()) {
            playOutCutscene(this::nextLevel);
        }
    }

    public void onEnemyReachedEnd(GameEvent event) {
        inc("enemiesKilled", +1);

        inc("lives", -1);
        uiController.loseLife();

        if (geti("lives") == 0)
            showGameOver();

        if (geti("enemiesKilled") == ENEMIES_PER_LEVEL)
            nextLevel();
    }

    public void onBonusPickup(BonusPickupEvent event) {
        switch (event.getType()) {
            case ATTACK_RATE:
                playerComponent.increaseAttackSpeed(PLAYER_BONUS_ATTACK_SPEED);
                break;
            case LIFE:
                inc("lives", +1);
                uiController.addLife();
                break;
            case BOMB:
                killRandomEnemy();
                break;
        }
    }

    private void killRandomEnemy() {
        FXGLMath.random(getGameWorld().getEntitiesByType(SpaceInvadersType.ENEMY))
                .flatMap(e -> e.getComponentOptional(EnemyComponent.class))
                .ifPresent(e -> e.die());
    }

    private void showGameOver() {
        getDialogService().showConfirmationBox("Demo Over. Play Again?", yes -> {
            if (yes) {
                getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
                getGameController().startNewGame();
            } else {

                int score = geti("score");

                if (score > highScore) {
                    getDialogService().showInputBox("High Score! Enter your name", playerName -> {

                        // we have to use file system directly, since we are running without menus
                        getFileSystemService().writeDataTask(new SaveData(playerName, score), SAVE_DATA_NAME).run();

                        getGameController().exit();
                    });
                } else {
                    getGameController().exit();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
