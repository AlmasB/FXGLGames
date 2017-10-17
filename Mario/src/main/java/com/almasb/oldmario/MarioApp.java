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

package com.almasb.oldmario;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.effect.Vignette;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.ScrollingBackgroundView;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.oldmario.event.PickupEvent;
import com.almasb.oldmario.event.CheckpointEvent;
import com.almasb.oldmario.event.DeathEvent;
import com.almasb.oldmario.event.Events;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class MarioApp extends GameApplication {

    public static final int BLOCK_SIZE = 32;

    private GameEntity player;
    private SControl playerControl;

    public GameEntity getPlayer() {
        return player;
    }

    private List<Point2D> arrowTrajectory = new ArrayList<>();

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
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initAssets() {
        getAssetLoader().cache();
    }

    @Override
    protected void initGame() {
        ui = new UIOverlay(getWidth(), getHeight());
        gameState = new GameState(this, ui);

        TextLevelParser levelParser = new TextLevelParser(getGameWorld().getEntityFactory());

        Level level = levelParser.parse("levels/0.txt");

        player = (GameEntity) level.getEntities().stream().filter(e -> e.hasControl(SControl.class)).findAny().get();
        //playerControl = player.getControl(PlayerControl.class);
        playerControl = player.getControl(SControl.class);

        gameState.updateCheckpoint(player.getPositionComponent().getValue());

        getGameWorld().setLevel(level);

        getGameScene().addGameView(new ScrollingBackgroundView(getAssetLoader().loadTexture("bg_wrap.png", 1280, 720),
                Orientation.HORIZONTAL));

        initEventHandlers();

        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);
        getGameScene().getViewport().setBounds(0, 0, level.getWidth(), level.getHeight());
    }

    @Override
    protected void initPhysics() {


//        getPhysicsWorld().addCollisionHandler(new PlayerEnemyHandler());
//        getPhysicsWorld().addCollisionHandler(new ProjectileEnemyHandler());
//        getPhysicsWorld().addCollisionHandler(new PlayerProjectileHandler());
    }

    private void initEventHandlers() {
        getEventBus().addEventHandler(PickupEvent.COIN, event -> {
            gameState.addScoreFromCoin(event.getPickup());
            event.getPickup().removeFromWorld();
        });

        getEventBus().addEventHandler(CheckpointEvent.ANY, event -> {
            event.getCheckpoint().getComponent(CollidableComponent.class).setValue(false);

            Point2D position = event.getCheckpoint().getComponent(PositionComponent.class).getValue();
            gameState.updateCheckpoint(position);

            ParticleEmitter emitter = ParticleEmitters.newImplosionEmitter();
            emitter.setColor(Color.GOLD);

            Entities.builder()
                    .at(position)
                    .with(new ParticleControl(emitter))
                    .with(new ExpireCleanControl(Duration.seconds(0.8)))
                    .buildAndAttach(getGameWorld());
        });

        getEventBus().addEventHandler(DeathEvent.ANY, event -> {
            gameState.loseLife();
            player.getControl(com.almasb.fxgl.physics.PhysicsControl.class)
                    .reposition(gameState.getCheckpoint());

            ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(15);
            emitter.setColor(Color.GOLD);

            Entities.builder()
                    .at(gameState.getCheckpoint())
                    .with(new ParticleControl(emitter))
                    .with(new ExpireCleanControl(Duration.seconds(0.8)))
                    .buildAndAttach(getGameWorld());
        });

        getEventBus().addEventHandler(Events.GAME_OVER, event -> {
            getDisplay().showMessageBox("Demo Over. Thanks for playing!", this::exit);
        });

        getEventBus().addEventHandler(Events.REACH_FINISH, event -> {
            getDisplay().showMessageBox("Demo Over. Thanks for playing!", this::exit);
        });
    }

    @Override
    protected void initUI() {
        Vignette vignette = new Vignette(1280, 720, 600);
        vignette.setIntensity(0.25);

        getGameScene().addUINodes(ui, vignette);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.D);
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
    protected void onUpdate(double tpf) {}

    //double dx, dy;

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
