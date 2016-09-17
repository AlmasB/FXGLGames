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
package com.almasb.geowars;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.geowars.component.OldPositionComponent;
import com.almasb.geowars.control.BulletControl;
import com.almasb.geowars.control.SeekerControl;
import com.almasb.geowars.control.WandererControl;
import com.almasb.geowars.component.GraphicsComponent;
import com.almasb.geowars.grid.Grid;
import com.almasb.geowars.control.GraphicsUpdateControl;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GeoWarsApp extends GameApplication {

    private enum Type {
        PLAYER, WANDERER, SEEKER, BULLET,
        SHOCKWAVE,
        EXPLOSION,
        PARTICLE
    }

    private GameEntity player;

    private Grid grid;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("0.4");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                Entities.getPosition(player).translate(-5, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                Entities.getPosition(player).translate(5, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                Entities.getPosition(player).translate(0, -5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                Entities.getPosition(player).translate(0, 5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            private LocalTimer timer = FXGL.newLocalTimer();

            @Override
            protected void onAction() {
                if (timer.elapsed(Duration.seconds(0.17))) {
                    shoot();
                    timer.capture();
                }
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0.3);
        getAudioPlayer().setGlobalMusicVolume(0.3);

        initBackground();
        initPlayer();

        getMasterTimer().runAtInterval(this::spawnWanderer, Duration.seconds(2));
        getMasterTimer().runAtInterval(this::spawnSeeker, Duration.seconds(5));

        getAudioPlayer().playMusic("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(Type.BULLET, Type.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                spawnExplosion(Entities.getBBox(b).getCenterWorld());

                a.removeFromWorld();
                b.removeFromWorld();
                addScoreKill();
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(Type.BULLET, Type.SEEKER));

        CollisionHandler playerEnemy = new CollisionHandler(Type.PLAYER, Type.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                Entities.getPosition(a).setValue(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(Type.PLAYER, Type.SEEKER));

        CollisionHandler shockEnemy = new CollisionHandler(Type.SHOCKWAVE, Type.SEEKER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                PositionComponent pos = Entities.getPosition(b);

                pos.translate(pos.getValue()
                        .subtract(Entities.getPosition(player).getValue())
                        .normalize()
                        .multiply(100));
            }
        };

        physics.addCollisionHandler(shockEnemy);
        physics.addCollisionHandler(shockEnemy.copyFor(Type.SHOCKWAVE, Type.WANDERER));
    }

    private IntegerProperty score = new SimpleIntegerProperty(0);

    @Override
    protected void initUI() {
        Text scoreText = getUIFactory().newText("", Color.WHITE, 18);
        scoreText.setTranslateX(1100);
        scoreText.setTranslateY(50);
        scoreText.textProperty().bind(score.asString("Score: %d"));

        getGameScene().addUINodes(scoreText);
    }

    @Override
    protected void onUpdate(double tpf) {
        player.getComponentUnsafe(OldPositionComponent.class)
                .setValue(Entities.getPosition(player).getValue());

        grid.update();
    }

    private void initBackground() {
        GameEntity bg = new GameEntity();
        bg.getMainViewComponent().setTexture("background.png");

        getGameWorld().addEntity(bg);

        Canvas canvas = new Canvas(getWidth(), getHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.118, 0.118, 0.545, 1));

        GameEntity e = new GameEntity();
        e.addComponent(new GraphicsComponent(canvas.getGraphicsContext2D()));
        e.addControl(new GraphicsUpdateControl());
        e.getMainViewComponent().setView(canvas);

        getGameWorld().addEntity(e);

        Rectangle size = new Rectangle(0, 0, 1280, 720);
        Point2D spacing = new Point2D(40, 40);

        grid = new Grid(size, spacing, getGameWorld(), canvas.getGraphicsContext2D());
    }

    private void initPlayer() {
        player = new GameEntity();
        player.getTypeComponent().setValue(Type.PLAYER);
        player.getPositionComponent().setValue(getWidth() / 2, getHeight() / 2);
        player.addComponent(new CollidableComponent(true));

        OldPositionComponent oldPosition = new OldPositionComponent();
        oldPosition.valueProperty().addListener((obs, old, newPos) -> {
            Entities.getRotation(player).rotateToVector(newPos.subtract(old));
        });

        player.addComponent(oldPosition);
        player.getMainViewComponent().setView(new EntityView(getAssetLoader().loadTexture("Player.png")), true);

        getGameWorld().addEntity(player);
    }

    private void spawnWanderer() {
        GameEntity wanderer = new GameEntity();
        wanderer.getTypeComponent().setValue(Type.WANDERER);
        wanderer.getPositionComponent().setValue(50, 50);
        wanderer.addControl(new WandererControl((int)getWidth(), (int)getHeight()));
        wanderer.addComponent(new CollidableComponent(true));
        wanderer.getMainViewComponent().setView(new EntityView(getAssetLoader().loadTexture("Wanderer.png")), true);

        getGameWorld().addEntity(wanderer);
    }

    private void spawnSeeker() {
        GameEntity seeker = new GameEntity();
        seeker.getTypeComponent().setValue(Type.SEEKER);
        seeker.getPositionComponent().setValue(50, 50);
        seeker.addControl(new SeekerControl(player));
        seeker.addComponent(new CollidableComponent(true));
        seeker.getMainViewComponent().setView(new EntityView(getAssetLoader().loadTexture("Seeker.png")), true);

        getGameWorld().addEntity(seeker);
    }

    private void spawnExplosion(Point2D point) {
        GameEntity explosion = new GameEntity();
        explosion.getPositionComponent().setValue(point.subtract(40, 40));
        explosion.getMainViewComponent().setView(getAssetLoader().loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)));
        explosion.addControl(new ExpireCleanControl(Duration.seconds(1.8)));

        getGameWorld().addEntity(explosion);

        getAudioPlayer().playSound("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");
    }

    private void shoot() {
        GameEntity bullet = new GameEntity();
        bullet.getTypeComponent().setValue(Type.BULLET);
        bullet.getPositionComponent().setValue(Entities.getBBox(player).getCenterWorld().subtract(14, 4.5));
        bullet.addControl(new ProjectileControl(getInput().getVectorToMouse(bullet.getPositionComponent().getValue()), 10 * 60));
        bullet.addControl(new BulletControl(grid));
        bullet.addControl(new OffscreenCleanControl());
        bullet.getMainViewComponent().setView(new EntityView(getAssetLoader().loadTexture("Bullet.png")), true);
        bullet.addComponent(new CollidableComponent(true));

        getGameWorld().addEntity(bullet);

        getAudioPlayer().playSound("shoot" + (int) (Math.random() * 8 + 1) + ".wav");
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getWidth(), Math.random() * getHeight());
    }

    private void addScoreKill() {
        score.set(score.get() + 100);
    }

    private void deductScoreDeath() {
        score.set(score.get() - 1000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
