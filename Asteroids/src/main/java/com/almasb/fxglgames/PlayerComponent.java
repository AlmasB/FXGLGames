package com.almasb.fxglgames;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(entity.getCenter());
    }

    public void rotateLeft() {
        entity.rotateBy(-5);
    }

    public void rotateRight() {
        entity.rotateBy(5);
    }

    public void move() {
        Vec2 dir = Vec2.fromAngle(entity.getRotation() - 90)
                .mulLocal(4);
        entity.translate(dir);
    }

    public void shoot() {
        Point2D center = entity.getCenter().subtract(37/2.0, 13/2.0);

        Vec2 dir = Vec2.fromAngle(entity.getRotation() - 90);

        spawn("bullet", new SpawnData(center.getX(), center.getY()).put("dir", dir.toPoint2D()));
    }
}
