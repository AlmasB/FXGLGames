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

package com.almasb.bomberman;

import com.almasb.bomberman.control.PlayerControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.TextLevelParser;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.gameutils.math.GameMath;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BombermanApp extends GameApplication {

    public static final int TILE_SIZE = 80;

    private GameEntity player;
    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BombermanApp");
        settings.setVersion("0.1");
        settings.setWidth(10 * TILE_SIZE);
        settings.setHeight(10 * TILE_SIZE);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                playerControl.moveUp();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                playerControl.moveLeft();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Down") {
            @Override
            protected void onActionBegin() {
                playerControl.moveDown();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                playerControl.moveRight();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Place Bomb") {
            @Override
            protected void onActionBegin() {
                playerControl.placeBomb();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    private BombermanFactory entityFactory;

    public BombermanFactory getEntityFactory() {
        return entityFactory;
    }

    @Override
    protected void initGame() {
        entityFactory = new BombermanFactory();

        TextLevelParser levelParser = new TextLevelParser(entityFactory);

        Level level = levelParser.parse("levels/0.txt");

        player = entityFactory.newPlayer(0, 0);
        playerControl = player.getControlUnsafe(PlayerControl.class);
        level.getEntities().add(player);

        getGameWorld().setLevel(level);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.POWERUP) {
            @Override
            protected void onCollisionBegin(Entity pl, Entity powerup) {
                powerup.removeFromWorld();
                playerControl.increaseMaxBombs();
            }
        });
    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public void onWallDestroyed(Entity wall) {
        if (GameMath.randomBoolean()) {
            int x = Entities.getPosition(wall).getGridX(BombermanApp.TILE_SIZE);
            int y = Entities.getPosition(wall).getGridY(BombermanApp.TILE_SIZE);

            GameEntity powerup = entityFactory.newPowerup(x, y);
            getGameWorld().addEntity(powerup);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
