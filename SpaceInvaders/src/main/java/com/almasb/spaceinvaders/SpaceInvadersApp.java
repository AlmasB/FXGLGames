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

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UIFactory;
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

import static com.almasb.spaceinvaders.EntityFactory.Type;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    private static final String SAVE_DATA_NAME = "hiscore.dat";

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Space Invaders");
        settings.setVersion("0.3dev");
        settings.setWidth(600);
        settings.setHeight(800);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
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

    private Entity player;
    private IntegerProperty enemiesDestroyed;
    private IntegerProperty score;
    private IntegerProperty level;
    private IntegerProperty lives;

    private int highScore;
    private String highScoreName;

    @Override
    protected void preInit() {
        getAudioPlayer().setGlobalSoundVolume(0.33);

        SaveData data = getSaveLoadManager().<SaveData>load(SAVE_DATA_NAME)
                .orElse(new SaveData("CPU", 10000));

        highScoreName = data.getName();
        highScore = data.getHighScore();
    }

    @Override
    protected void initGame() {
        initBackground();

        getNotificationService().setBackgroundColor(Color.DARKBLUE);

        enemiesDestroyed = new SimpleIntegerProperty(0);
        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);

        getAchievementManager().getAchievementByName("Hitman")
                .achievedProperty().bind(enemiesDestroyed.greaterThanOrEqualTo(5));
        getAchievementManager().getAchievementByName("Master Scorer")
                .achievedProperty().bind(score.greaterThanOrEqualTo(10000));

        spawnPlayer();
        nextLevel();
    }

    private void initBackground() {
        Entity bg = Entity.noType();
        Texture bgTexture = getAssetLoader().loadTexture("background.png");
        bgTexture.setFitWidth(getWidth());
        bgTexture.setFitHeight(getHeight());

        bg.setSceneView(bgTexture);

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

        PhysicsEntity levelInfo = new PhysicsEntity(Type.LEVEL_INFO);
        levelInfo.setPosition(getWidth() / 2 - UIFactory.widthOf("Level " + level.get(), 44) / 2, 0);
        levelInfo.setSceneView(UIFactory.newText("Level " + level.get(), Color.AQUAMARINE, 44));
        levelInfo.setBodyType(BodyType.DYNAMIC);
        levelInfo.setOnPhysicsInitialized(() -> levelInfo.setLinearVelocity(0, 5));
        levelInfo.setExpireTime(Duration.seconds(3));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.setDensity(0.05f);
        fixtureDef.setRestitution(0.3f);
        levelInfo.setFixtureDef(fixtureDef);

        PhysicsEntity ground = new PhysicsEntity(Type.LEVEL_INFO);
        ground.setPosition(0, getHeight() / 2);
        ground.setSceneView(new Rectangle(getWidth(), 100, Color.TRANSPARENT));
        ground.setExpireTime(Duration.seconds(3));

        getGameWorld().addEntities(levelInfo, ground);

        getMasterTimer().runOnceAfter(this::initLevel, Duration.seconds(3));

        playSound("level.wav");
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        physicsWorld.addCollisionHandler(new CollisionHandler(Type.BULLET, Type.PLAYER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity player) {
                // player shot that bullet so no need to handle collision
                if (bullet.getComponentUnsafe(OwnerComponent.class).getValue().isType(Type.PLAYER)) {
                    return;
                }

                bullet.removeFromWorld();
                lives.set(lives.get() - 1);
                if (lives.get() == 0)
                    showGameOver();
            }
        });

        physicsWorld.addCollisionHandler(new CollisionHandler(Type.BULLET, Type.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                // some enemy shot the bullet, skip collision handling
                if (bullet.getComponentUnsafe(OwnerComponent.class).getValue().isType(Type.ENEMY)) {
                    return;
                }

                bullet.removeFromWorld();
                enemy.removeFromWorld();
                enemiesDestroyed.set(enemiesDestroyed.get() + 1);
                score.set(score.get() + 200);

                Entity explosion = EntityFactory.newExplosion(enemy.getCenter());
                getGameWorld().addEntity(explosion);

                playSound("explosion.wav");

                if (enemiesDestroyed.get() % 40 == 0)
                    nextLevel();
            }
        });
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

                    playSound("lose_life.wav");
                }
            });

            getGameScene().addUINode(t);
        }

        HBox textBox = new HBox(20, textScore, textHighScore);
        textBox.setTranslateX(25);
        textBox.setTranslateY(10);

        getGameScene().addUINodes(textBox);
    }

    @Override
    protected void onUpdate() {
        removeOffscreenBullets();
    }

    private void removeOffscreenBullets() {
        getGameWorld().getEntities(Type.BULLET)
                .stream()
                .filter(b -> b.isOutside(0, 0, getWidth(), getHeight()))
                .forEach(Entity::removeFromWorld);
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
        if (player.getX() >= 5)
            player.translate(-5, 0);
    }

    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
    public void moveRight() {
        if (player.getX() <= getWidth() - player.getWidth() - 5)
            player.translate(5, 0);
    }

    @OnUserAction(name = "Shoot", type = ActionType.ON_ACTION_BEGIN)
    public void shoot() {
        Entity bullet = EntityFactory.newBullet(player);

        getGameWorld().addEntity(bullet);

        playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    private void playSound(String name) {
        getAudioPlayer().playSound(getAssetLoader().loadSound(name));
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
