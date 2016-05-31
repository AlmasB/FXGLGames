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

package com.almasb.mario;

import java.util.ArrayList;
import java.util.List;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.mario.EntityType.Type;

public class Physics {

    private GameApplication app;
    private Entity[][] grid;

    public Physics(GameApplication app) {
        this.app = app;
    }

    public void setGrid(Entity[][] grid) {
        this.grid = grid;
    }

    /**
     * Returns true iff entity has moved value units
     *
     * @param e
     * @param value
     * @return
     */
    public boolean moveX(Entity e, int value) {
//        boolean movingRight = value > 0;
//
////        double w = e.getLayoutBounds().getWidth();
////        double h = e.getLayoutBounds().getHeight();
//
//        //Rectangle2D broadphase = new Rectangle2D(e.getTranslateX() - w / 2, e.getTranslateY() - h / 2, 2*w, 2*h);
//
//        List<Entity> platforms = app.getSceneManager().getEntities(Type.PLATFORM);
//
//        for (int i = 0; i < Math.abs(value); i++) {
//            for (Entity platform : platforms) {
//                // broadphase
////                if (platform.getTranslateX() > e.getTranslateX() + w * 2
////                        || platform.getTranslateX() + platform.getWidth() < e.getTranslateX() - w * 2) {
////                    continue;
////                }
//
//                // narrowphase
//                if (e.getBoundsInParent().intersects(platform.getBoundsInParent())) {
//                    if (movingRight) {
//                        if (e.getTranslateX() + e.getWidth() == platform.getTranslateX()) {
//                            //app.triggerCollision(e, platform);
//                            e.translate(-1, 0);
//
//                            return false;
//                        }
//                    }
//                    else {
//                        if (e.getTranslateX() == platform.getTranslateX() + platform.getWidth()) {
//                            //app.triggerCollision(e, platform);
//                            e.translate(1, 0);
//
//
//                            return false;
//                        }
//                    }
//                }
//            }
//            e.setTranslateX(e.getTranslateX() + (movingRight ? 1 : -1));
//        }

        return true;
    }

    public void moveY_new(Entity e, int value) {
//        boolean movingDown = value > 0;
//
//        int x1 = (int)e.getTranslateX() / MarioApp.BLOCK_SIZE;
//        int x2 = (int)(e.getTranslateX() + e.getLayoutBounds().getWidth()) / MarioApp.BLOCK_SIZE;
//        int y1 = (int)e.getTranslateY() / MarioApp.BLOCK_SIZE - 1;
//        int y2 = (int)(e.getTranslateY() + e.getLayoutBounds().getHeight()) / MarioApp.BLOCK_SIZE + 1;
//
//        List<Entity> platforms = collect(x1, x2, y1, y2);
//
//        for (int i = 0; i < Math.abs(value); i++) {
//            for (Entity platform : platforms) {
//                if (e.getBoundsInParent().intersects(platform.getBoundsInParent())) {
//                    if (movingDown) {
//                        if (e.getTranslateY() + e.getHeight() == platform.getTranslateY()) {
//                            //app.triggerCollision(e, platform);
//                            e.setTranslateY(e.getTranslateY() - 1);
//                            e.setProperty("jumping", false);
//                            return;
//                        }
//                    }
//                    else {
//                        if (e.getTranslateY() == platform.getTranslateY() + platform.getHeight()) {
//                            //app.triggerCollision(e, platform);
//                            return;
//                        }
//                    }
//                }
//            }
//            e.setTranslateY(e.getTranslateY() + (movingDown ? 1 : -1));
//            e.setProperty("jumping", true);
//        }
    }

    public void moveY(Entity e, int value) {
//        boolean movingDown = value > 0;
//
////        double w = e.getLayoutBounds().getWidth();
////        double h = e.getLayoutBounds().getHeight();
//
//        List<Entity> platforms = app.getSceneManager().getEntities(Type.PLATFORM);
//
//        for (int i = 0; i < Math.abs(value); i++) {
//            for (Entity platform : platforms) {
////                // broadphase
////                if (platform.getTranslateX() > e.getTranslateX() + w * 2
////                        || platform.getTranslateX()
////                                + platform.getWidth() < e.getTranslateX()
////                                        - w * 2) {
////                    continue;
////                }
//
//                if (e.getBoundsInParent().intersects(platform.getBoundsInParent())) {
//                    if (movingDown) {
//                        if (e.getTranslateY() + e.getHeight() == platform.getTranslateY()) {
//                            //app.triggerCollision(e, platform);
//                            e.setTranslateY(e.getTranslateY() - 1);
//                            e.setProperty("jumping", false);
//                            return;
//                        }
//                    }
//                    else {
//                        if (e.getTranslateY() == platform.getTranslateY() + platform.getHeight()) {
//                            //app.triggerCollision(e, platform);
//                            return;
//                        }
//                    }
//                }
//            }
//            e.setTranslateY(e.getTranslateY() + (movingDown ? 1 : -1));
//            e.setProperty("jumping", true);
//        }
    }

    private List<Entity> collect(int x1, int x2, int y1, int y2) {
        x1 = Math.max(x1, 0);
        y1 = Math.max(y1, 0);
        x2 = Math.min(x2, grid.length - 1);
        y2 = Math.min(y2, grid[0].length - 1);

        List<Entity> list = new ArrayList<>();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (grid[x][y] != null) {
                    list.add(grid[x][y]);
                }
            }
        }

        return list;
    }
}
