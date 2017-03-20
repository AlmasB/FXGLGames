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

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.ui.WheelMenu;
import com.almasb.geowars.component.GraphicsComponent;
import com.almasb.geowars.component.OldPositionComponent;
import com.almasb.geowars.control.GraphicsUpdateControl;
import com.almasb.geowars.control.PlayerControl;
import com.almasb.geowars.control.RicochetControl;
import com.almasb.geowars.grid.Grid;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GeoWarsApp extends GameApplication {

    private GameEntity player;
    private PlayerControl playerControl;

    private Grid grid;

    public GameEntity getPlayer() {
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
        settings.setVersion("0.5");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
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

        input.addAction(new UserAction("Shoot") {
            private LocalTimer timer = FXGL.newLocalTimer();

            @Override
            protected void onAction() {
                if (timer.elapsed(Duration.seconds(0.17))) {
                    Point2D position = player.getCenter().subtract(14, 4.5);

                    GameEntity bullet = getGameWorld().<GeoWarsFactory>getEntityFactory().spawnBullet(position, input.getVectorToMouse(position));

                    if (getGameState().getObject("weaponType") == WeaponType.RICOCHET) {
                        bullet.removeControl(OffscreenCleanControl.class);
                        bullet.addControl(new RicochetControl());
                    }

                    if (getGameState().getObject("weaponType") == WeaponType.BEAM) {
                        Point2D toMouse = input.getVectorToMouse(position).normalize().multiply(10);
                        Point2D pos = position;

                        for (int i = 0; i < 7; i++) {
                            getGameWorld().<GeoWarsFactory>getEntityFactory().spawnBullet(pos.add(toMouse), toMouse);
                            pos = pos.add(toMouse);
                        }
                    }

                    if (getGameState().getObject("weaponType") == WeaponType.WAVE) {
                        Point2D toMouse = input.getVectorToMouse(position);
                        double baseAngle = new Vec2(toMouse.getX(), toMouse.getY()).angle() + 45;
                        for (int i = 0; i < 7; i++) {
                            Vec2 vec = Vec2.fromAngle(baseAngle);
                            getGameWorld().<GeoWarsFactory>getEntityFactory().spawnBullet(position, new Point2D(vec.x, vec.y));

                            baseAngle += 45;
                        }
                    }

                    timer.capture();
                }
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Weapon Menu") {
            @Override
            protected void onActionBegin() {
                openWeaponMenu();
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("time", 30);
        vars.put("weaponType", WeaponType.NORMAL);
    }

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0.0);
        getAudioPlayer().setGlobalMusicVolume(0.0);

        initBackground();
        player = (GameEntity) getGameWorld().spawn("Player");
        playerControl = player.getControlUnsafe(PlayerControl.class);

        //getMasterTimer().runAtInterval(() -> getGameWorld().spawn("Wanderer"), Duration.seconds(2));
        //getMasterTimer().runAtInterval(() -> getGameWorld().spawn("Seeker"), Duration.seconds(4));
        getMasterTimer().runAtInterval(() -> getGameState().increment("time", -1), Duration.seconds(1));

        getAudioPlayer().playMusic("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(GeoWarsType.BULLET, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity wanderer) {
                getGameWorld().spawn("Explosion", Entities.getBBox(wanderer).getCenterWorld());

                bullet.removeFromWorld();
                wanderer.removeFromWorld();
                addScoreKill();
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(GeoWarsType.BULLET, GeoWarsType.SEEKER));

        CollisionHandler playerEnemy = new CollisionHandler(GeoWarsType.PLAYER, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                Entities.getPosition(a).setValue(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(GeoWarsType.PLAYER, GeoWarsType.SEEKER));

        CollisionHandler shockEnemy = new CollisionHandler(GeoWarsType.SHOCKWAVE, GeoWarsType.SEEKER) {
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
        physics.addCollisionHandler(shockEnemy.copyFor(GeoWarsType.SHOCKWAVE, GeoWarsType.WANDERER));
    }

    private WheelMenu weaponMenu;

    @Override
    protected void initUI() {
        Text scoreText = getUIFactory().newText("", Color.WHITE, 18);
        scoreText.setTranslateX(1100);
        scoreText.setTranslateY(50);
        scoreText.textProperty().bind(getGameState().intProperty("score").asString("Score: %d"));

        Text timerText = getUIFactory().newText("", Color.WHITE, 18);
        timerText.layoutBoundsProperty().addListener((o, old, bounds) -> {
            timerText.setTranslateX(getWidth() / 2 - bounds.getWidth() / 2);
        });

        timerText.setTranslateX(getWidth() / 2);
        timerText.setTranslateY(60);
        timerText.textProperty().bind(getGameState().intProperty("time").asString());

        Circle timerCircle = new Circle(40, 40, 40, null);
        timerCircle.setStrokeWidth(2);
        timerCircle.setStroke(Color.AQUA);
        timerCircle.setTranslateX(getWidth() / 2 - 40);
        timerCircle.setTranslateY(60 - 40 - 5);

        WeaponType[] weaponTypes = WeaponType.values();

        weaponMenu = new WheelMenu(
                weaponTypes[0].toString(),
                weaponTypes[1].toString(),
                weaponTypes[2].toString(),
                weaponTypes[3].toString()
        );

        weaponMenu.setSelectionHandler(typeName -> {
            WeaponType type = WeaponType.valueOf(typeName);
            getGameState().setValue("weaponType", type);
            getAudioPlayer().playSound(typeName.toLowerCase() + ".wav");
        });

        getGameScene().addUINodes(scoreText, timerText, timerCircle, weaponMenu);

        weaponMenu.close();
    }

    @Override
    protected void onUpdate(double tpf) {
        player.getComponentUnsafe(OldPositionComponent.class)
                .setValue(Entities.getPosition(player).getValue());

        grid.update();
    }

    private void initBackground() {
        GameEntity bg = new GameEntity();
        bg.getViewComponent().setTexture("background.png");

        getGameWorld().addEntity(bg);

        Canvas canvas = new Canvas(getWidth(), getHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.118, 0.118, 0.545, 1));

        GameEntity e = new GameEntity();
        e.addComponent(new GraphicsComponent(canvas.getGraphicsContext2D()));
        e.addControl(new GraphicsUpdateControl());
        e.getViewComponent().setView(canvas);

        getGameWorld().addEntity(e);

        Rectangle size = new Rectangle(0, 0, 1280, 720);
        Point2D spacing = new Point2D(40, 40);

        grid = new Grid(size, spacing, getGameWorld(), canvas.getGraphicsContext2D());
    }

    private void openWeaponMenu() {
        weaponMenu.setTranslateX(getInput().getMouseXWorld() - 20);
        weaponMenu.setTranslateY(getInput().getMouseYWorld() - 70);
        weaponMenu.open();
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getWidth(), Math.random() * getHeight());
    }

    private void addScoreKill() {
        getGameState().increment("score", +100);
    }

    private void deductScoreDeath() {
        getGameState().increment("score", -1000);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
