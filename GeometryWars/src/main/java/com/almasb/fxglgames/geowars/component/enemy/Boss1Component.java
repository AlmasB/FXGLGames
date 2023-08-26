package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.factory.EnemyFactory;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Boss1Component extends Component {

    private List<Entity> guards = new ArrayList<>();

    private List<Point2D> wayPoints = new ArrayList<>();

    private int index = 0;

    public Boss1Component() {
        double distance = 300;

        wayPoints.add(new Point2D(distance, distance));
        wayPoints.add(new Point2D(getAppWidth() - distance, getAppHeight() - distance));
        wayPoints.add(new Point2D(getAppWidth() - distance, distance));
        wayPoints.add(new Point2D(distance, getAppHeight() - distance));
    }

    @Override
    public void onUpdate(double tpf) {
        guards.forEach(g -> g.getComponent(CircularMoveComponent.class).setCenter(entity.getCenter().subtract(30, 30)));
    }

    public void moveToNextPoint() {
        int newIndex;

        do {
            newIndex = FXGLMath.random(0, wayPoints.size() - 1);
        } while (newIndex == index);

        index = newIndex;

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .onFinished(() -> {
                    if (entity != null && entity.isActive())
                        moveToNextPoint();
                })
                .duration(Duration.seconds(3))
                .translate(entity)
                .to(wayPoints.get(index).subtract(entity.getWidth() / 2, entity.getHeight() / 2))
                .buildAndPlay();
    }

    public void spawnGuards() {
        moveToNextPoint();

        var c = entity.getCenter();

        var numGuards = 100;
        var anglePerGuard = 360.0 / numGuards;

        for (int angle = 0; angle <= 360; angle += anglePerGuard) {
            var vector = Vec2.fromAngle(angle).mulLocal(160);

            // E1
            var guardPos = vector.add(c);

            var e1 = spawn("Wanderer");
            EnemyFactory.respawnWandererGuard(e1);
            e1.addComponent(new CircularMoveComponent(c.subtract(30, 30), angle, 130, true));

            // E2
            guardPos.addLocal(vector.mul(1.5));

            var e2 = spawn("Wanderer");
            EnemyFactory.respawnWandererGuard(e2);
            e2.addComponent(new CircularMoveComponent(c.subtract(30, 30), angle, 270, false));

            // E3
            guardPos.addLocal(vector.mul(1.5));

            var e3 = spawn("Wanderer");
            EnemyFactory.respawnWandererGuard(e3);
            e3.addComponent(new CircularMoveComponent(c.subtract(30, 30), angle, 410, true));

            guards.addAll(List.of(e1, e2, e3));
        }
    }

    @Override
    public void onRemoved() {
        guards.forEach(g -> {
            if (g.isActive())
                g.removeFromWorld();
        });
    }
}
