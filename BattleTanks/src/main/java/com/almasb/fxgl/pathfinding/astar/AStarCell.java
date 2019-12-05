/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarCell {

    private AStarCell parent;

    private CellState state;

    private int x;
    private int y;

    private int gCost;
    private int hCost;

    private Object userData = null;

    public AStarCell(int x, int y, CellState state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    /**
     * Set user specific data.
     */
    public final void setUserData(Object userData) {
        this.userData = userData;
    }

    /**
     * @return user specific data
     */
    public final Object getUserData() {
        return userData;
    }

    public final void setParent(AStarCell parent) {
        this.parent = parent;
    }

    public final AStarCell getParent() {
        return parent;
    }

    public final void setHCost(int hCost) {
        this.hCost = hCost;
    }

    public final int getHCost() {
        return hCost;
    }

    public final void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public final int getGCost() {
        return gCost;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final void setState(CellState state) {
        this.state = state;
    }

    public final CellState getState() {
        return state;
    }

    /**
     * @return F cost (G + H)
     */
    public final int getFCost() {
        return gCost + hCost;
    }

    @Override
    public String toString() {
        return "A* Node[x=" + x + ",y=" + y + "," + state + "]";
    }
}
