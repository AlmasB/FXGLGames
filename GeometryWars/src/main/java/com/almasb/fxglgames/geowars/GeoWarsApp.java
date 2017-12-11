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

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.event.Handles;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.WheelMenu;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import com.almasb.fxglgames.geowars.component.HPComponent;
import com.almasb.fxglgames.geowars.component.OldPositionComponent;
import com.almasb.fxglgames.geowars.control.GraphicsUpdateControl;
import com.almasb.fxglgames.geowars.control.PlayerControl;
import com.almasb.fxglgames.geowars.event.DeathEvent;
import com.almasb.fxglgames.geowars.grid.Grid;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GeoWarsApp extends GameApplication {

    private Entity player;
    private PlayerControl playerControl;

    private Grid grid;

    public Entity getPlayer() {
        return player;
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("0.7.5");
    }

    @Override
    protected void initAssets() {
        // preload explosion sprite sheet
        getAssetLoader().loadTexture("explosion.png", 80 * 48, 80);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shockwave") {
            @Override
            protected void onActionBegin() {
                playerControl.releaseShockwave();
            }
        }, KeyCode.E);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                playerControl.shoot(input.getMousePositionWorld());
            }
        }, MouseButton.PRIMARY);
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
        getAudioPlayer().setGlobalSoundVolume(0.2);
        getAudioPlayer().setGlobalMusicVolume(0.2);

        initBackground();
        player = spawn("Player");
        playerControl = player.getControl(PlayerControl.class);

        getGameState().<Integer>addListener("multiplier", (prev, now) -> {
            WeaponType current = geto("weaponType");
            WeaponType newType = WeaponType.fromMultiplier(geti("multiplier"));

            if (newType.isBetterThan(current)) {
                set("weaponType", newType);
            }
        });

        getMasterTimer().runAtInterval(() -> spawn("Wanderer"), Duration.seconds(1.5));
        getMasterTimer().runAtInterval(() -> spawn("Seeker"), Duration.seconds(3));
        getMasterTimer().runAtInterval(() -> spawn("Runner"), Duration.seconds(5));
        getMasterTimer().runAtInterval(() -> spawn("Bouncer"), Duration.seconds(5));
        getMasterTimer().runAtInterval(() -> spawn("Portal", getRandomPoint()), Duration.seconds(5));
        getMasterTimer().runAtInterval(() -> inc("time", -1), Duration.seconds(1));

        loopBGM("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(GeoWarsType.BULLET, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                HPComponent hp = enemy.getComponent(HPComponent.class);
                hp.setValue(hp.getValue() - 1);

                if (hp.getValue() == 0) {
                    getEventBus().fireEvent(new DeathEvent(enemy));
                    enemy.removeFromWorld();
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(GeoWarsType.BULLET, GeoWarsType.SEEKER));
        physics.addCollisionHandler(bulletEnemy.copyFor(GeoWarsType.BULLET, GeoWarsType.RUNNER));
        physics.addCollisionHandler(bulletEnemy.copyFor(GeoWarsType.BULLET, GeoWarsType.BOUNCER));

        CollisionHandler playerEnemy = new CollisionHandler(GeoWarsType.PLAYER, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                getGameScene().getViewport().shake(8);

                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(GeoWarsType.PLAYER, GeoWarsType.SEEKER));
        physics.addCollisionHandler(playerEnemy.copyFor(GeoWarsType.PLAYER, GeoWarsType.RUNNER));
        physics.addCollisionHandler(playerEnemy.copyFor(GeoWarsType.PLAYER, GeoWarsType.BOUNCER));
    }

    @Override
    protected void initUI() {
        Text scoreText = getUIFactory().newText("", Color.WHITE, 28);
        scoreText.setTranslateX(1100);
        scoreText.setTranslateY(70);
        scoreText.textProperty().bind(getip("score").asString());

        Text multText = getUIFactory().newText("", Color.WHITE, 28);
        multText.setTranslateX(60);
        multText.setTranslateY(70);
        multText.textProperty().bind(getip("multiplier").asString("x %d"));

        Text timerText = getUIFactory().newText("", Color.WHITE, 28);
        timerText.layoutBoundsProperty().addListener((o, old, bounds) -> {
            timerText.setTranslateX(getWidth() / 2 - bounds.getWidth() / 2);
        });

        timerText.setTranslateX(getWidth() / 2);
        timerText.setTranslateY(60);
        timerText.textProperty().bind(getip("time").asString());

        Circle timerCircle = new Circle(40, 40, 40, null);
        timerCircle.setStrokeWidth(2);
        timerCircle.setStroke(Color.AQUA);
        timerCircle.setTranslateX(getWidth() / 2 - 40);
        timerCircle.setTranslateY(60 - 40 - 5);

        getGameScene().addUINodes(multText, scoreText, timerText, timerCircle);

        Text beware = getUIFactory().newText("Beware! Seekers get smarter every time!", Color.AQUA, 38);
        beware.setOpacity(0);

        getGameScene().addUINode(beware);

        getUIFactory().centerText(beware);
        getUIFactory().fadeInOut(beware, Duration.seconds(2), () -> getGameScene().removeUINode(beware)).startInPlayState();

        getGameScene().setCursor("crosshair.png", new Point2D(32, 32));
    }

    @Override
    protected void onUpdate(double tpf) {
        player.getComponent(OldPositionComponent.class).setValue(player.getPosition());

        grid.update();
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (geti("time") == 0) {
            getDisplay().showMessageBox("Demo Over. Your score: " + getGameState().getInt("score"), this::exit);
        }
    }

    private void initBackground() {
        EntityView backgroundView = new EntityView(new Rectangle(getWidth(), getHeight()));
        getGameScene().addGameView(backgroundView);

        Canvas canvas = new Canvas(getWidth(), getHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.138, 0.138, 0.375, 0.3));

        Entities.builder()
                .viewFromNode(canvas)
                .with(new GraphicsComponent(canvas.getGraphicsContext2D()))
                .with(new GraphicsUpdateControl())
                .buildAndAttach(getGameWorld());

        Rectangle size = new Rectangle(0, 0, getWidth(), getHeight());
        Point2D spacing = new Point2D(38.8, 40);

        grid = new Grid(size, spacing, getGameWorld(), canvas.getGraphicsContext2D());
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getWidth(), Math.random() * getHeight());
    }

    private void addScoreKill(Point2D enemyPosition) {
        inc("kills", +1);

        if (geti("kills") == 15) {
            set("kills", 0);
            inc("multiplier", +1);
        }

        final int multiplier = geti("multiplier");

        Text bonusText = getUIFactory().newText("+100" + (multiplier == 1 ? "" : "x" + multiplier), Color.color(1, 1, 1, 0.8), 24);

        getGameScene().addUINode(bonusText);

        Animation<?> animation = getUIFactory().translate(bonusText, enemyPosition, new Point2D(1100, 70), Duration.seconds(1));
        animation.getAnimatedValue().setInterpolator(Interpolators.EXPONENTIAL.EASE_IN());
        animation.setOnFinished(() -> {
            inc("score", +100*multiplier);
            getGameScene().removeUINode(bonusText);
        });
        animation.startInPlayState();
    }

    private void deductScoreDeath() {
        inc("score", -1000);
        set("kills", 0);
        set("multiplier", 1);

        Text bonusText = getUIFactory().newText("-1000", Color.WHITE, 20);
        bonusText.setTranslateX(1100);
        bonusText.setTranslateY(70);

        getGameScene().addUINode(bonusText);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), bonusText);
        tt.setToY(0);
        tt.setOnFinished(e -> {
            getGameScene().removeUINode(bonusText);
        });
        tt.play();
    }

    @Handles(eventType = "ANY")
    public void onDeath(DeathEvent event) {
        Entity entity = event.getEntity();

        if (event.isEnemy()) {
            spawn("Explosion", entity.getCenter());
            spawn("Crystal", entity.getCenter());

            addScoreKill(entity.getCenter());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
