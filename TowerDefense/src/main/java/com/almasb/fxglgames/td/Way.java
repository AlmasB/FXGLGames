package com.almasb.fxglgames.td;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Way {

    private List<Point2D> waypoints;

    public Way(List<Point2D> waypoints) {
        this.waypoints = waypoints;
    }

    public List<Point2D> getWaypoints() {
        return new ArrayList<>(waypoints);
    }

    public static Way fromPolygon(Polygon polygon, double offsetX, double offsetY) {
        var list = new ArrayList<Point2D>();

        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            var x = polygon.getPoints().get(i) + offsetX;
            var y = polygon.getPoints().get(i+1) + offsetY;

            list.add(new Point2D(x, y));
        }

        return new Way(list);
    }
}
