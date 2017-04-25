package com.almasb.fxglgames.td.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.td.TowerDefenseApp;
import com.almasb.fxglgames.td.event.EnemyReachedGoalEvent;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    private List<Point2D> waypoints;
    private Point2D nextWaypoint;

    private PositionComponent position;

    private double speed;

    @Override
    public void onAdded(Entity entity) {
        waypoints = ((TowerDefenseApp) FXGL.getApp()).getWaypoints();
        position = Entities.getPosition(entity);

        nextWaypoint = waypoints.remove(0);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        speed = tpf * 60 * 2;

        Point2D velocity = nextWaypoint.subtract(position.getValue())
                .normalize()
                .multiply(speed);

        position.translate(velocity);

        if (nextWaypoint.distance(position.getValue()) < speed) {
            position.setValue(nextWaypoint);

            if (!waypoints.isEmpty()) {
                nextWaypoint = waypoints.remove(0);
            } else {

                FXGL.getEventBus().fireEvent(new EnemyReachedGoalEvent());
            }
        }
    }
}
