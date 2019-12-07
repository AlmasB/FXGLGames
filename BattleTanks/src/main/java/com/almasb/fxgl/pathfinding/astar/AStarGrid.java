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
}
