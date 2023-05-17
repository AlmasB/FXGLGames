package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.WaypointMoveComponent;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.factory.EnemyFactory;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Boss1Component extends Component {

    public void spawnGuards() {
        var c = entity.getCenter();

        var numGuards = 100;
        var anglePerGuard = 360.0 / numGuards;

        var points = new ArrayList<Point2D>();

        for (int angle = 0; angle <= 360; angle += anglePerGuard) {
            var vector = Vec2.fromAngle(angle).mulLocal(160);
            var guardPos = vector.add(c).sub(30, 30);

            points.add(guardPos.toPoint2D());
        }


        for (int angle = 0; angle <= 360; angle += anglePerGuard) {
            var vector = Vec2.fromAngle(angle).mulLocal(160);

            // E1
            var guardPos = vector.add(c);

            var e1 = spawn("Wanderer");
            e1.setPosition(guardPos.x - 30, guardPos.y - 30);
            e1.getComponent(WandererComponent.class).pause();

            e1.addComponent(new CircularMoveComponent(c.subtract(30, 30), angle));


//            animationBuilder()
//                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
//                    .delay(Duration.seconds(0.5 + angle / 360.0))
//                    .duration(Duration.seconds(3))
//                    .repeatInfinitely()
//                    .rotate(e1)
//                    .origin(new Point2D(c.getX() - guardPos.x + 30, c.getY() - guardPos.y + 30))
//                    .from(0)
//                    .to(360)
//                    .buildAndPlay();

            // E2
//            guardPos.addLocal(vector.mul(1.5));
//
//            var e2 = spawn("Wanderer");
//            e2.setPosition(guardPos.x - 30, guardPos.y - 30);
//            e2.getComponent(WandererComponent.class).pause();
//
//            animationBuilder()
//                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
//                    .delay(Duration.seconds(0.5 + angle / 180.0))
//                    .duration(Duration.seconds(3))
//                    .repeatInfinitely()
//                    .rotate(e2)
//                    .origin(new Point2D(c.getX() - guardPos.x + 30, c.getY() - guardPos.y + 30))
//                    .from(360)
//                    .to(0)
//                    .buildAndPlay();
//
//            // E3
//            guardPos.addLocal(vector.mul(1.5));
//
//            var e3 = spawn("Wanderer");
//            e3.setPosition(guardPos.x - 30, guardPos.y - 30);
//            e3.getComponent(WandererComponent.class).pause();
//
//            animationBuilder()
//                    .duration(Duration.seconds(25))
//                    .repeatInfinitely()
//                    .rotate(e3)
//                    .origin(new Point2D(c.getX() - guardPos.x + 30, c.getY() - guardPos.y + 30))
//                    .from(0)
//                    .to(360)
//                    .buildAndPlay();
        }
    }
}
