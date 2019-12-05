/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding;

import com.almasb.fxgl.core.util.Consumer;

import java.lang.reflect.Array;
import java.util.function.BiFunction;

/**
 * TODO: final methods or not?
 *
 * @param <T> cell type
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Grid<T> {

    private T[][] data;

    private int width;
    private int height;

    public Grid(int width, int height, Class<T> type) {
        this(width, height, type, (x, y) -> null);
    }

    @SuppressWarnings("unchecked")
    public Grid(int width, int height, Class<T> type, BiFunction<Integer, Integer, T> initFunction) {
        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException("Cannot create grid with 0 or negative size");

        this.width = width;
        this.height = height;

        //data = (T[][]) new Object[width][height];

        data = (T[][]) Array.newInstance(type, width, height);

        populate(initFunction);
    }

    public void populate(BiFunction<Integer, Integer, T> populateFunction) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                set(x, y, populateFunction.apply(x, y));
            }
        }
    }

    /**
     * @return number of cells in X direction
     */
    public int getWidth() {
        // data.length
        return width;
    }

    /**
     * @return number of cells in Y direction
     */
    public int getHeight() {
        // data[0].length
        return height;
    }

    /**
     * Checks if given (x,y) is within the bounds of the grid,
     * i.e. get(x, y) won't return OOB.
     *
     * @return true IFF the point is within the grid
     */
    public final boolean isWithin(int x, int y) {
        return x >= 0 && x < getWidth()
                && y >= 0 && y < getHeight();
    }

    public T[][] getData() {
        return data;
    }

    public T get(int x, int y) {
        return data[x][y];
    }

    public void set(int x, int y, T node) {
        data[x][y] = node;
    }

    public void forEach(Consumer<T> function) {
        for (int y = 0; y < data[0].length; y++) {
            for (int x = 0; x < data.length; x++) {
                function.accept(get(x, y));
            }
        }
    }

//    public void forEach(TriConsumer<T, Integer, Integer> function) {
//        for (int y = 0; y < data[0].length; y++) {
//            for (int x = 0; x < data.length; x++) {
//                function.accept(get(x, y), x, y);
//            }
//        }
//    }
}