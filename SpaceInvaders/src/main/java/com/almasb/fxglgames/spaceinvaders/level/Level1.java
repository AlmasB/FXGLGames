package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;
import static java.lang.Math.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level1 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                final int finalY = y;

                FXGL.getMasterTimer().runOnceAfter(() -> {

                    Entity enemy = spawnEnemy(50, 50);

                    enemy.addControl(new ButterflyControl());

//                    Animation<?> anim = Entities.animationBuilder()
//                            .autoReverse(true)
//                            .interpolator(Interpolators.CIRCULAR.EASE_IN_OUT())
//                            .duration(Duration.seconds(3))
//                            .repeat(Integer.MAX_VALUE)
//                            .translate(enemy)
//                            .from(enemy.getPosition())
//                            .to(new Point2D(toRight ? Config.WIDTH - 50 - 40 : 50, FXGLMath.random(50, Config.HEIGHT / 2)))
//                            .buildAndPlay();
//
//                    animations.add(anim);

                }, Duration.seconds(t));

                t += 0.1;
            }
        }
    }

    @Override
    public void destroy() {
        animations.forEach(Animation::stop);
    }

    private class ButterflyControl extends Control {

        private double t = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {
            Entities.getPosition(entity).setValue(curveFunction().add(FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2 - 100));

            t += tpf;
        }

        private Point2D curveFunction() {
            double x = sin(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));
            double y = cos(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));

            return new Point2D(x, -y).multiply(85);
        }
    }
}
