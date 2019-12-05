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
import com.almasb.fxglgames.tanks.collision.BulletEnemyFlagHandler;
import com.almasb.fxglgames.tanks.collision.BulletEnemyTankHandler;
import com.almasb.fxglgames.tanks.components.TankViewComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.tanks.BattleTanksType.*;

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
        settings.setExperimentalNative(true);
        settings.setDeveloperMenuEnabled(true);
    }

    private TankViewComponent tankViewComponent;

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                tankViewComponent.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                tankViewComponent.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                tankViewComponent.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                tankViewComponent.down();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                tankViewComponent.shoot();
            }
        }, KeyCode.F);

        input.addAction(new UserAction("Move To") {
            @Override
            protected void onActionBegin() {
                tankViewComponent.getEntity().call("moveTo", 2, 3);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        getGameWorld().addEntityFactory(new BattleTanksFactory());

        setLevelFromMap("tmx/level2.tmx");

        tankViewComponent = getGameWorld().getSingleton(PLAYER).getComponent(TankViewComponent.class);
    }

    @Override
    protected void initPhysics() {
        var bulletTankHandler = new BulletEnemyTankHandler();

        getPhysicsWorld().addCollisionHandler(bulletTankHandler);
        getPhysicsWorld().addCollisionHandler(bulletTankHandler.copyFor(BULLET, PLAYER));

        var bulletFlagHandler = new BulletEnemyFlagHandler();

        getPhysicsWorld().addCollisionHandler(bulletFlagHandler);
        getPhysicsWorld().addCollisionHandler(bulletFlagHandler.copyFor(BULLET, PLAYER_FLAG));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
