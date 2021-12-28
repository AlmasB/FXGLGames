package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.td.TowerDefenseApp;
import com.almasb.fxglgames.td.data.EnemyData;
import com.almasb.fxglgames.td.data.Way;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyComponent extends Component {

    private List<Point2D> waypoints;
    private EnemyData data;
    private Point2D nextWaypoint;

    public EnemyComponent(Way way, EnemyData data) {
        waypoints = way.getWaypoints();
        this.data = data;
    }

    public EnemyData getData() {
        return data;
    }

    @Override
    public void onAdded() {
        nextWaypoint = waypoints.remove(0);

        entity.setPosition(nextWaypoint);
    }

    @Override
    public void onUpdate(double tpf) {
        double speed = tpf * 60 * data.moveSpeed();

        Point2D velocity = nextWaypoint.subtract(entity.getPosition())
                .normalize()
                .multiply(speed);

        entity.translate(velocity);

        if (nextWaypoint.distance(entity.getPosition()) < speed) {
            entity.setPosition(nextWaypoint);

            if (!waypoints.isEmpty()) {
                nextWaypoint = waypoints.remove(0);
            } else {
                FXGL.<TowerDefenseApp>getAppCast().onEnemyReachedEnd(entity);

                entity.removeFromWorld();
            }
        }
    }
}
