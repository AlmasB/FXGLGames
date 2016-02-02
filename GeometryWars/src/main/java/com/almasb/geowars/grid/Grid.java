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

package com.almasb.geowars.grid;

import com.almasb.ents.Entity;
import com.almasb.fxgl.gameplay.GameWorld;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Grid {

    private static final double POINT_MASS_DAMPING = 0.8;
    private static final double SPRING_STIFFNESS = 0.28;
    private static final double SPRING_DAMPING = 0.06;

    private List<Spring> springs = new ArrayList<>();
    private PointMass[][] points;

    private GraphicsContext g;

    public Grid(Rectangle size, Point2D spacing, GameWorld world, GraphicsContext g) {
        int numColumns = (int) (size.getWidth() / spacing.getX()) + 2;
        int numRows = (int) (size.getHeight() / spacing.getY()) + 2;
        points = new PointMass[numColumns][numRows];

        this.g = g;

        PointMass[][] fixedPoints = new PointMass[numColumns][numRows];

        // create the point masses
        float xCoord = 0, yCoord = 0;
        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                points[column][row] = new PointMass(new Point2D(xCoord, yCoord), POINT_MASS_DAMPING, 1);
                fixedPoints[column][row] = new PointMass(new Point2D(xCoord, yCoord), POINT_MASS_DAMPING, 0);
                xCoord += spacing.getX();
            }
            yCoord += spacing.getY();
            xCoord = 0;
        }

        // link the point masses with springs
        for (int y = 0; y < numRows; y++) {
            for (int x = 0; x < numColumns; x++) {
                if (x == 0 || y == 0 || x == numColumns - 1 || y == numRows - 1) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.5, 0.1, false, null, world));
                } else if (x % 3 == 0 && y % 3 == 0) {
                    springs.add(new Spring(fixedPoints[x][y], points[x][y], 0.005, 0.02, false, null, world));
                }

                if (x > 0) {
                    springs.add(new Spring(points[x - 1][y], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING, true,
                            createLine(y % 3 == 0 ? 3 : 1), world));
                }

                if (y > 0) {
                    springs.add(new Spring(points[x][y - 1], points[x][y], SPRING_STIFFNESS, SPRING_DAMPING, true,
                            createLine(x % 3 == 0 ? 3 : 1), world));
                }

                // add additional lines
                if (x > 0 && y > 0) {
                    Entity additionalLine = createLine(1);
                    additionalLine.addControl(new AdditionalLineControl(
                            points[x - 1][y], points[x][y],
                            points[x - 1][y - 1], points[x][y - 1]));
                    world.addEntity(additionalLine);

                    Entity additionalLine2 = createLine(1);
                    additionalLine2.addControl(new AdditionalLineControl(
                            points[x][y - 1], points[x][y],
                            points[x - 1][y - 1], points[x - 1][y]));
                    world.addEntity(additionalLine2);
                }
            }
        }
    }

    private Entity createLine(float thickness) {
        Entity entity = new Entity();

        Line line = new Line(0, 0, 1, 0);
        line.setStrokeWidth(thickness);
        line.setStroke(new Color(0.118, 0.118, 0.545, 0.65));

        entity.addComponent(new GraphicsComponent(g));

        return entity;
    }

    public void update() {
        springs.forEach(Spring::update);

        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                points[x][y].update();
            }
        }
    }

    public void applyDirectedForce(Point2D force, Point2D position, float radius) {
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                if (position.distance(points[x][y].getPosition()) * position.distance(points[x][y].getPosition())
                        < radius * radius) {
                    double forceFactor = 10 / (10 + position.distance(points[x][y].getPosition()));
                    points[x][y].applyForce(force.multiply(forceFactor));
                }
            }
        }
    }

    public void applyImplosiveForce(double force, Point2D position, float radius) {
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                double dist = position.distance(points[x][y].getPosition());
                dist *= dist;
                if (dist < radius * radius) {
                    Point2D forceVec = position.subtract(points[x][y].getPosition());
                    forceVec = forceVec.multiply(1f * force / (100 + dist));
                    points[x][y].applyForce(forceVec);
                    points[x][y].increaseDamping(0.6f);
                }
            }
        }
    }

    public void applyExplosiveForce(double force, Point2D position, double radius) {
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[0].length; y++) {
                double dist = position.distance(points[x][y].getPosition());
                dist *= dist;
                if (dist < radius * radius) {
                    Point2D forceVec = position.subtract(points[x][y].getPosition());
                    forceVec = forceVec.multiply(-10f * force / (10000 + dist));
                    points[x][y].applyForce(forceVec);
                    points[x][y].increaseDamping(0.6f);
                }
            }
        }
    }
}