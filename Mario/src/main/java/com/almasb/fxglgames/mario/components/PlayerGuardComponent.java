package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerGuardComponent extends Component {

    private Entity player;

    private int maxDistance = 100;

    private Point2D nextPoint;

    private LocalTimer moveTimer;

    @Override
    public void onAdded() {
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
        player = FXGL.geto("player");
        nextPoint = player.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        if (moveTimer.elapsed(Duration.seconds(1.3))) {

            Point2D vector = FXGLMath.randomPoint2D();

            nextPoint = player.getCenter().add(vector.multiply(FXGLMath.random(60, maxDistance)));

            FXGL.animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .translate(entity)
                    .alongPath(new QuadCurve(entity.getX(), entity.getY(), player.getX(), player.getY(), nextPoint.getX(), nextPoint.getY()))
                    .buildAndPlay();

            moveTimer.capture();
        }
    }
}
