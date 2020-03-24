package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.*;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level15 extends SpaceLevel {

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                FXGL.getGameTimer().runOnceAfter(() -> {

                    Entity enemy = spawnEnemy(50, 50);

                    enemy.addComponent(new MoveComponent());

                }, Duration.seconds(t));

                t += 0.33;
            }
        }
    }

    private static class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(curveFunction().add(FXGL.getAppWidth() / 2.0 - 600, FXGL.getAppHeight() / 2 - 300));

            t += tpf;
        }

        private Point2D curveFunction() {
            double cos = cos(t);
            double sin = sin(t);

            double tx = FXGLMath.map(cos, -1, 1, 0, 1);
            double ty = FXGLMath.map(sin, -1, 1, 0, 1);
            tx = Interpolators.BOUNCE.easeOut(tx);
            ty = Interpolators.BOUNCE.easeIn(ty);

            double x = cos(tx) * 3;
            double y = 1 - sin(3*ty);

            return new Point2D(x, y).multiply(250);
        }
    }
}
