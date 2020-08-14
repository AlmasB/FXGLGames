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

package com.almasb.fxglgames.gravity;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxglgames.gravity.scifi.ScifiType;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GravityApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(640);
        settings.setHeight(640);
        settings.setTitle("GravityApp");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setDeveloperMenuEnabled(true);
    }

    private Entity player;
    //private PlayerComponent playerComponent;

    //                getGameWorld().getCollidingEntities(player)
//                        .stream()
//                        .filter(e -> e.hasControl(UsableControl.class))
//                        .map(e -> e.getControlUnsafe(UsableControl.class))
//                        .forEach(UsableControl::use);


    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Pull") {
            @Override
            protected void onActionBegin() {

                Entity key = getGameWorld().getEntitiesByType(ScifiType.ENEMY).get(0);

                PhysicsComponent physics = key.getComponent(PhysicsComponent.class);
                physics.applyForceToCenter(player.getCenter().subtract(key.getCenter()).normalize().multiply(550));
            }
        }, KeyCode.Q);

        getInput().addAction(new UserAction("Push") {
            @Override
            protected void onActionBegin() {

                Entity key = getGameWorld().getEntitiesByType(ScifiType.ENEMY).get(0);

                PhysicsComponent physics = key.getComponent(PhysicsComponent.class);
                physics.applyForceToCenter(player.getCenter().subtract(key.getCenter()).normalize().multiply(-550));
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                //playerComponent.left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                //playerComponent.right();
            }
        }, KeyCode.D);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GravityFactory());

        //getGameWorld().setLevel(getAssetLoader().loadLevel("test_level.json", new TMXLevelLoader()));

        getGameWorld().spawn("button", 30, 340);

        player = getGameWorld().spawn("player", 100, 100);
        //playerComponent = player.getControl(PlayerComponent.class);

        getGameWorld().spawn("key", 500, 10);

        getGameWorld().spawn("enemy", 150, 100);

        getGameWorld().addEntity(entityBuilder().buildScreenBounds(40));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
