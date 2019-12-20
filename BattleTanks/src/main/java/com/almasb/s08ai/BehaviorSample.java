/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.sandbox.DeveloperWASDControl;
import com.almasb.fxgl.ai.btree.BehaviorTreeComponent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows to how use gdxAI BehaviorTree.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BehaviorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BehaviorSample");
        settings.setVersion("0.1");
    }

    Entity player;

    @Override
    protected void initInput() {
        Input input = getInput();

//        input.addAction(new UserAction("Move Left") {
//            @Override
//            protected void onAction() {
//                playerControl.left();
//            }
//        }, KeyCode.A);
//
//        input.addAction(new UserAction("Move Right") {
//            @Override
//            protected void onAction() {
//                playerControl.right();
//            }
//        }, KeyCode.D);
//
//        input.addAction(new UserAction("Move Up") {
//            @Override
//            protected void onAction() {
//                playerControl.up();
//            }
//        }, KeyCode.W);
//
//        input.addAction(new UserAction("Move Down") {
//            @Override
//            protected void onAction() {
//                playerControl.down();
//            }
//        }, KeyCode.S);
    }

    @Override
    protected void initGame() {
        player = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        entityBuilder()
                .at(400, 100)
                .view(new Rectangle(40, 40, Color.RED))
                .with(new BehaviorTreeComponent("patrol.tree"))
                .buildAndAttach();

        entityBuilder()
                .at(600, 100)
                .view(new Rectangle(40, 40, Color.LIGHTGOLDENRODYELLOW))
                .with(new BehaviorTreeComponent("patrol.tree"))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
