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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BattleTanksApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BattleTanks");
        settings.setVersion("0.2");
        settings.setWidth(40 * 32);
        settings.setHeight(40 * 18);
    }

    private PlayerComponent playerComponent;

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerComponent.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerComponent.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerComponent.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerComponent.down();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                playerComponent.shoot();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BattleTanksFactory());

        setLevelFromMap("tmx/level1.tmx");

        playerComponent = new PlayerComponent();

        Entity player = new Entity();
        player.getBoundingBoxComponent().addHitBox(new HitBox("BODY", new Point2D(10, 10), BoundingShape.box(64, 64)));
        player.addComponent(new MoveComponent());
        player.addComponent(playerComponent);
        player.getTransformComponent().setScaleOrigin(new Point2D(42, 42));
        player.setScaleX(0.3);
        player.setScaleY(0.3);

        getGameWorld().addEntity(player);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(BattleTanksType.BULLET, BattleTanksType.WALL) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity wall) {
                bullet.removeFromWorld();
                wall.removeFromWorld();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
