///*
// * The MIT License (MIT)
// *
// * FXGL - JavaFX Game Library
// *
// * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.almasb.fxglgames.pacman.ai;
//
//import com.almasb.fxgl.ai.GoalAction;
//import com.almasb.fxgl.app.FXGL;
//import com.almasb.fxgl.core.math.FXGLMath;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.extra.ai.pathfinding.NodeState;
//import com.almasb.fxgl.time.LocalTimer;
//import com.almasb.fxglgames.pacman.PacmanApp;
//import com.almasb.fxglgames.pacman.PacmanType;
//import com.almasb.fxglgames.pacman.control.MoveControl;
//import com.almasb.fxglgames.pacman.control.MoveDirection;
//import javafx.geometry.Point2D;
//import javafx.util.Duration;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Random;
//
///**
// * @author Almas Baimagambetov (almaslvl@gmail.com)
// */
//public class RandomMoveAction extends GoalAction {
//
//    private LocalTimer timer = FXGL.newLocalTimer();
//
//    public RandomMoveAction() {
//        super("RandomMove");
//    }
//
//    @Override
//    public void start() {
//        timer.capture();
//        getEntity().getComponent(MoveControl.class).setMoveDirection(FXGLMath.random(MoveDirection.values()).get());
//    }
//
//    @Override
//    public boolean reachedGoal() {
//        return timer.elapsed(Duration.seconds(2));
//    }
//
//    @Override
//    public void end() {
//        FXGL.<PacmanApp>getAppCast()
//                .getGrid()
//                .getNodes()
//                .stream()
//                .filter(n -> n.getState() == NodeState.WALKABLE)
//                .sorted(Comparator.comparingDouble(n -> getEntity().getPosition().distance(n.getX() * PacmanApp.BLOCK_SIZE, n.getY() * PacmanApp.BLOCK_SIZE)))
//                .findFirst()
//                .ifPresent(n -> {
//                    getEntity().setPosition(n.getX() * PacmanApp.BLOCK_SIZE, n.getY() * PacmanApp.BLOCK_SIZE);
//                });
//    }
//
//    @Override
//    public void onUpdate(double tpf) {
//
//    }
//}
