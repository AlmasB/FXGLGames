/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.mario;

import java.util.ArrayList;
import java.util.List;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.mario.collision.PlayerCheckpointHandler;
import com.almasb.mario.collision.PlayerEnemyHandler;
import com.almasb.mario.collision.PlayerFinishHandler;
import com.almasb.mario.collision.PlayerPickupHandler;
import com.almasb.mario.collision.PlayerProjectileHandler;
import com.almasb.mario.collision.ProjectileEnemyHandler;
import com.almasb.mario.control.PhysicsControl;
import com.almasb.mario.control.SeekingControl;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class MarioApp extends GameApplication {

    public static final int BLOCK_SIZE = 32;

    private LevelParser parser;
    private Level level;

    private Physics physics = new Physics(this);

    private Entity player;

    private List<Point2D> arrowTrajectory = new ArrayList<>();

    private Entity[][] grid;

    private GameState gameState;
    private UIOverlay ui;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Mario");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
    }

    @Override
    protected void initAssets() {
        //assets = assetManager.cache();
    }

    @Override
    protected void initGame() {
//        ui = new UIOverlay(getWidth(), getHeight(), assets);
//        gameState = new GameState(this, ui);
//
//        parser = new LevelParser(assets);
//        level = parser.getLevel();
//        player = parser.getPlayer();
//        player.addControl(new PhysicsControl(physics));
//        gameState.updateCheckpoint(player.getPosition());
//
//        grid = new Entity[level.getWidth()][level.getHeight()];
//        physics.setGrid(grid);
//
//        //getSceneManager().addEntities(Entity.noType().setGraphics(new Rectangle(1280, 720)));
//
//        for (Entity e : level.getEntities()) {
//            if (e.isType(Type.PLATFORM)) {
//                int x = (int)e.getTranslateX() / BLOCK_SIZE;
//                int y = (int)e.getTranslateY() / BLOCK_SIZE;
//                grid[x][y] = e;
//            }
//
//            if (e.isType(Type.ENEMY)) {
//                e.addFXGLEventHandler(Event.ENEMY_ATTACK, event -> {
//                    getSceneManager().addEntities(event.getSource());
//                });
//                e.addFXGLEventHandler(Event.HIT_BY_GHOST_BOMB, hitByGhostBomb);
//            }
//
//            getSceneManager().addEntities(e);
//        }
//
////        ParticleEntity e = new ParticleEntity(Type.PROJECTILE);
////        e.setPosition(180, 600);
////        //e.addControl(new CircularMovementControl(10, 50));
////        e.setGraphics(new Circle(1));
////
////        SmokeEmitter emitter = new SmokeEmitter();
////        //emitter.setInitialVelocityFunction(() -> new Point2D(Math.random() *-2, 0));
////        //emitter.setGravity(Math.random() * -1, -0);
////        e.setEmitter(emitter);
////
////        e.translateXProperty().bind(player.translateXProperty().add(5));
////        e.translateYProperty().bind(player.translateYProperty());
////
////        sceneManager.addEntities(e);
//
//        initEventHandlers();
//        sceneManager.bindViewportOriginX(player, (int)getWidth() / 2);
//        sceneManager.setUIMouseTransparent(true);
    }

    @Override
    protected void initPhysics() {
//        physicsManager.addCollisionHandler(new PlayerPickupHandler());
//        physicsManager.addCollisionHandler(new PlayerCheckpointHandler());
//        physicsManager.addCollisionHandler(new PlayerFinishHandler());
//        physicsManager.addCollisionHandler(new PlayerEnemyHandler());
//        physicsManager.addCollisionHandler(new ProjectileEnemyHandler());
//        physicsManager.addCollisionHandler(new PlayerProjectileHandler());
    }

    private void initEventHandlers() {
//        player.addFXGLEventHandler(Event.DEATH, event -> {
//            gameState.loseLife();
//            player.setPosition(gameState.getCheckpoint());
//            particleManager.spawnExplosion(gameState.getCheckpoint().add(16, 16), Color.GOLD, 40, 40);
//        });
//
//        player.addFXGLEventHandler(Event.GAME_OVER, event -> {
//            gameState.gameLose();
//        });
//
//        player.addFXGLEventHandler(Event.PICKUP_COIN, event -> {
//            Entity coin = event.getSource();
//            sceneManager.removeEntity(coin);
//            gameState.addScoreFromCoin(coin);
//        });
//
//        player.addFXGLEventHandler(Event.PICKUP_GHOST_BOMB, event -> {
//            Entity bomb = event.getSource();
//            sceneManager.removeEntity(bomb);
//            gameState.addGhostBomb();
//        });
//
//        player.addFXGLEventHandler(Event.REACH_CHECKPOINT, event -> {
//            Entity checkpointEntity = event.getSource();
//            checkpointEntity.setCollidable(false);
//            Point2D pos = checkpointEntity.getPosition();
//
//            gameState.updateCheckpoint(pos);
//
//            particleManager.spawnImplosion(pos.add(16, 16), Color.GOLD, 40, 40);
//        });
//
//        player.addFXGLEventHandler(Event.REACH_FINISH, event -> {
//            gameState.gameWin();
//        });
    }

    @Override
    protected void initUI() {
        //sceneManager.addUINodes(ui);
    }

    @Override
    protected void initInput() {
//        inputManager.addAction(new UserAction("Jump") {
//            @Override
//            protected void onActionBegin() {
//                player.getControl(PhysicsControl.class).jump();
//            }
//        }, KeyCode.W);
//
//        inputManager.addAction(new UserAction("Left") {
//            @Override
//            protected void onAction() {
//                player.setScaleX(-1);
//                physics.moveX(player, -5);
//            }
//        }, KeyCode.A);
//
//        inputManager.addAction(new UserAction("Right") {
//            @Override
//            protected void onAction() {
//                player.setScaleX(1);
//                physics.moveX(player, 5);
//            }
//        }, KeyCode.D);
//
//        inputManager.addAction(new UserAction("Spawn Bomb") {
//            @Override
//            protected void onActionBegin() {
//                if (gameState.hasGhostBombs()) {
//                    spawnGhostBomb();
//                    gameState.removeGhostBomb();
//                }
//                else {
//                    ui.showMessageFlash("NO BOMBS!");
//                }
//            }
//        }, KeyCode.F);
//
//        inputManager.addAction(new UserAction("Find Target") {
//            @Override
//            protected void onActionBegin() {
//                sceneManager.getEntitiesInRange(new Rectangle2D(mouse.x, mouse.y, BLOCK_SIZE, BLOCK_SIZE), Type.ENEMY).stream().findAny().ifPresent(e -> {
//                    sceneManager.getEntities(Type.PROJECTILE).stream().filter(p -> p.getProperty(Property.SUB_TYPE) == ProjectileType.GHOST_BOMB).findAny().ifPresent(p -> {
//                        p.addControl(new SeekingControl(e));
//                    });
//                });
//            }
//        }, MouseButton.SECONDARY);
//
//        inputManager.addAction(new UserAction("Range Test") {
//            @Override
//            protected void onActionBegin() {
//                sceneManager.getEntitiesInRange(player.computeRange(BLOCK_SIZE, BLOCK_SIZE), Type.PLATFORM).forEach(sceneManager::removeEntity);
//            }
//        }, KeyCode.T);
    }

    @Override
    protected void onUpdate(double tpf) {
//        if (player.getTranslateY() >= getHeight()) {
//            player.fireFXGLEvent(new FXGLEvent(Event.DEATH));
//        }
    }

    double dx, dy;

//    private void shoot() {
//        Entity arrow = new Entity(Type.PROJECTILE);
//        arrow.setProperty(Property.SUB_TYPE, ProjectileType.ARROW);
//        arrow.setPosition(player.getPosition().add(11, 15));
//        arrow.setGraphics(assets.getTexture("arrow2.png"));
//        arrow.setCollidable(true);
//        arrow.translateXProperty().addListener((obs, oldX, newX) -> {
//            dx = newX.doubleValue() - oldX.doubleValue();
//        });
//        arrow.translateYProperty().addListener((obs, oldY, newY) -> {
//            dy = newY.doubleValue() - oldY.doubleValue();
//        });
//
//        arrow.addControl(new Control() {
//            @Override
//            public void onUpdate(Entity entity, long now) {
//                entity.getTransforms().setAll(new Rotate(Math.toDegrees(Math.atan2(dy, dx)), 25, 4));
//            }
//        });
//
//        SequentialTransition st = new SequentialTransition();
//        st.setOnFinished(e -> sceneManager.removeEntity(arrow));
//
//        for (Point2D p : arrowTrajectory) {
//            TranslateTransition tt = new TranslateTransition(Duration.seconds(1), arrow);
//            tt.setToX(p.getX());
//            tt.setToY(p.getY());
//            st.getChildren().add(tt);
//        }
//        arrowTrajectory.clear();
//
//        sceneManager.addEntities(arrow);
//        st.play();
//    }
//
//    private void spawnGhostBomb() {
//        Entity bomb = new Entity(Type.PROJECTILE);
//        bomb.setProperty(Property.SUB_TYPE, ProjectileType.GHOST_BOMB);
//        bomb.setPosition(mouse.x, mouse.y);
//        bomb.setGraphics(assets.getTexture("ghost_bomb.png"));
//        bomb.setCollidable(true);
//
//        sceneManager.addEntities(bomb);
//    }
//
//    public FXGLEventHandler hitByArrow = event -> {
//        sceneManager.removeEntity(event.getTarget());
//        sceneManager.removeEntity(event.getSource());
//    };
//
//    public FXGLEventHandler hitByGhostBomb = event -> {
//        sceneManager.removeEntity(event.getTarget());
//        sceneManager.removeEntity(event.getSource());
//        particleManager.spawnExplosion(event.getSource().getPosition(), Color.BLUEVIOLET, 60, 40);
//    };

    public static void main(String[] args) {
        launch(args);
    }
}
