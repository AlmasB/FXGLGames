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
package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxglgames.pacman.components.PlayerComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.pacman.PacmanType.*;

/**
 * This is a basic demo of Pacman.
 *
 * Assets taken from opengameart.org
 * (Carlos Alface 2014 kalface@gmail.com, http://c-toy.blogspot.pt/).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PacmanApp extends GameApplication {

    public static final int BLOCK_SIZE = 40;

    public static final int MAP_SIZE = 21;

    private static final int UI_SIZE = 80;

    // seconds
    public static final int TIME_PER_LEVEL = 100;

    public Entity getPlayer() {
        return getGameWorld().getSingleton(PLAYER);
    }

    public PlayerComponent getPlayerComponent() {
        return getPlayer().getComponent(PlayerComponent.class);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(MAP_SIZE * BLOCK_SIZE + UI_SIZE);
        settings.setHeight(MAP_SIZE * BLOCK_SIZE);
        settings.setTitle("FXGL Pac-man");
        settings.setVersion("1.0");
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                getPlayerComponent().up();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                getPlayerComponent().down();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                getPlayerComponent().left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                getPlayerComponent().right();
            }
        }, KeyCode.D);
//
//        getInput().addAction(new UserAction("Teleport") {
//            @Override
//            protected void onActionBegin() {
//
//                if (geti("teleport") > 0) {
//                    inc("teleport", -1);
//                    getPlayerControl().teleport();
//                }
//            }
//        }, KeyCode.F);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("coins", 0);
        vars.put("teleport", 0);
        vars.put("time", TIME_PER_LEVEL);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKSLATEGREY);

        getGameWorld().addEntityFactory(new PacmanFactory());

        Level level = getAssetLoader().loadLevel("pacman_level0.txt", new TextLevelLoader(40, 40, ' '));
        getGameWorld().setLevel(level);

        // init the A* underlying grid and mark cells where blocks are as not walkable
        AStarGrid grid = AStarGrid.fromWorld(getGameWorld(), MAP_SIZE, MAP_SIZE, BLOCK_SIZE, BLOCK_SIZE, (type) -> {
            if (type == BLOCK)
                return CellState.NOT_WALKABLE;

            return CellState.WALKABLE;
        });

        set("grid", grid);

        // find out number of coins
        set("coins", getGameWorld().getEntitiesByType(COIN).size());

        run(() -> inc("time", -1), Duration.seconds(1));

        getWorldProperties().<Integer>addListener("time", (old, now) -> {
            if (now == 0) {
                onPlayerKilled();
            }
        });
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(PLAYER, ENEMY, (p, e) -> onPlayerKilled());

        onCollisionCollectible(PLAYER, COIN, c -> onCoinPickup());
    }

    @Override
    protected void initUI() {
        UI ui = getAssetLoader().loadUI("pacman_ui.fxml", new PacmanUIController());
        ui.getRoot().setTranslateX(MAP_SIZE * BLOCK_SIZE);

        getGameScene().addUI(ui);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            getGameController().startNewGame();
        }
    }

    public void onCoinPickup() {
        inc("coins", -1);
        inc("score", +50);

        if (geti("score") % 2000 == 0) {
            inc("teleport", +1);
        }

        if (geti("coins") == 0) {
            gameOver();
        }
    }

    private boolean requestNewGame = false;

    public void onPlayerKilled() {
        requestNewGame = true;
    }

    private void gameOver() {
        getDialogService().showMessageBox("Demo Over. Press OK to exit", getGameController()::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
