/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.Grid;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarGrid extends Grid<AStarCell> {

    /**
     * Constructs A* grid with A* cells using given width and height.
     * All cells are initially {@link CellState#WALKABLE}.
     */
    public AStarGrid(int width, int height) {
        super(width, height, AStarCell.class, (x, y) -> new AStarCell(x, y, CellState.WALKABLE));
    }



//    /**
//     * Convenience method to set state of all nodes to given state.
//     *
//     * @param state node state
//     */
//    public final void setStateForAllNodes(CellState state) {
//        for (int y = 0; y < getHeight(); y++) {
//            for (int x = 0; x < getWidth(); x++) {
//                getNode(x, y).setState(state);
//            }
//        }
//    }
//
//    /**
//     * Set state of the node at x, y.
//     *
//     * @param x the x coordinate
//     * @param y the y coordinate
//     * @param state the state
//     */
//    public final void setNodeState(int x, int y, CellState state) {
//        getNode(x, y).setState(state);
//    }
//
//    /**
//     * Returns state of the node at a, y.
//     *
//     * @param x the x coordinate
//     * @param y the y coordinate
//     * @return  the state
//     */
//    public final CellState getNodeState(int x, int y) {
//        return getNode(x, y).getState();
//    }
//
//
//

//
//    /**
//     * @return all grid nodes
//     */
//    public final List<AStarCell> getNodes() {
//        List<AStarCell> nodes = new ArrayList<>();
//
//        for (int y = 0; y < getHeight(); y++) {
//            for (int x = 0; x < getWidth(); x++) {
//                nodes.add(getNode(x, y));
//            }
//        }
//
//        return nodes;
//    }
}
