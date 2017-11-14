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

package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.AStarNode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.control.AStarMoveControl;
import com.almasb.fxglgames.pacman.control.MoveControl;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AStarMoveAction extends GoalAction {

    public AStarMoveAction() {
        super("Move");
    }

    private AStarGrid grid;
    private Entity player;

    private PositionComponent position;

    private int targetX;
    private int targetY;

    @Override
    public void start() {
        position = getEntity().getPositionComponent();

        player = ((PacmanApp) FXGL.getApp()).getPlayer();

        targetX = (int)((player.getX() + 20) / PacmanApp.BLOCK_SIZE);
        targetY = (int)((player.getY() + 20) / PacmanApp.BLOCK_SIZE);

        getEntity().getControl(AStarMoveControl.class).moveTo(new Point2D(targetX * PacmanApp.BLOCK_SIZE, targetY * PacmanApp.BLOCK_SIZE));
    }

    @Override
    public boolean reachedGoal() {
        return getEntity().getControl(AStarMoveControl.class).isDone();
    }

    @Override
    public void onUpdate(double tpf) {

    }
}
