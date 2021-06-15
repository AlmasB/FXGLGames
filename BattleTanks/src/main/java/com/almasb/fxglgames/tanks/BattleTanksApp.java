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

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.tanks.collision.BulletBrickHandler;
import com.almasb.fxglgames.tanks.collision.BulletEnemyTankHandler;
import com.almasb.fxglgames.tanks.collision.BulletFlagHandler;
import com.almasb.fxglgames.tanks.components.TankAIComponent;
import com.almasb.fxglgames.tanks.components.TankViewComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.tanks.BattleTanksType.*;
import static com.almasb.fxglgames.tanks.Config.BLOCK_SIZE;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BattleTanksApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BattleTanks");
        settings.setVersion("0.2");
        settings.setWidth(60 * 21);
        settings.setHeight(60 * 12);
        settings.setNative(true);
        settings.setDeveloperMenuEnabled(true);
    }

    private TankViewComponent tankViewComponent;

    private AStarGrid grid;

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                tankViewComponent.left();
            }
        }, KeyCode.A, VirtualButton.LEFT);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                tankViewComponent.right();
            }
        }, KeyCode.D, VirtualButton.RIGHT);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                tankViewComponent.up();
            }
        }, KeyCode.W, VirtualButton.UP);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                tankViewComponent.down();
            }
        }, KeyCode.S, VirtualButton.DOWN);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                tankViewComponent.shoot();
            }
        }, KeyCode.F);

        input.addAction(new UserAction("Move To") {
            @Override
            protected void onActionBegin() {
                tankViewComponent.getEntity().getComponent(AStarMoveComponent.class)
                        .moveToCell((int) (getInput().getMouseXWorld() / 30), (int) (getInput().getMouseYWorld() / 30));
                //tankViewComponent.getEntity().call("moveToCell", 2, 3);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        getGameWorld().addEntityFactory(new BattleTanksFactory());

        setLevelFromMap("tmx/level2.tmx");

        // TODO: careful: the world itself is 21x12 but each block we count as 2
        grid = AStarGrid.fromWorld(getGameWorld(), 21 * 2, 12 * 2, BLOCK_SIZE / 2, BLOCK_SIZE / 2, (type) -> {
            if (type == WALL || type == BRICK || type == PLAYER_FLAG || type == ENEMY_FLAG)
                return CellState.NOT_WALKABLE;

            return CellState.WALKABLE;
        });

        tankViewComponent = getGameWorld().getSingleton(PLAYER).getComponent(TankViewComponent.class);
        tankViewComponent.getEntity().addComponent(new AStarMoveComponent(grid));
        tankViewComponent.getEntity().removeComponent(TankAIComponent.class);

        byType(ENEMY).forEach(e -> {
            e.addComponent(new AStarMoveComponent(grid));
        });

        byType(ALLY).forEach(e -> {
            e.addComponent(new AStarMoveComponent(grid));
        });
    }

    @Override
    protected void initPhysics() {
        var bulletTankHandler = new BulletEnemyTankHandler();

        getPhysicsWorld().addCollisionHandler(bulletTankHandler);
        getPhysicsWorld().addCollisionHandler(bulletTankHandler.copyFor(BULLET, PLAYER));

        var bulletFlagHandler = new BulletFlagHandler();

        getPhysicsWorld().addCollisionHandler(bulletFlagHandler);
        getPhysicsWorld().addCollisionHandler(bulletFlagHandler.copyFor(BULLET, PLAYER_FLAG));

        getPhysicsWorld().addCollisionHandler(new BulletBrickHandler());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
