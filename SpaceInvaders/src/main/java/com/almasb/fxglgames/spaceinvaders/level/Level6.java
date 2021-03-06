package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level6 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0.0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                double px = random(0, getAppWidth() - 100);
                double py = random(0, getAppHeight() / 2.0);

                runOnce(() -> {
                    Entity enemy = spawnEnemy(px, py);

                    var a = animationBuilder()
                            .autoReverse(true)
                            .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                            .repeatInfinitely()
                            .duration(Duration.seconds(1.0))
                            .translate(enemy)
                            .from(new Point2D(px, py))
                            .to(new Point2D(random(0, getAppWidth() - 100), random(0, getAppHeight() / 2.0)))
                            .build();

                    animations.add(a);
                    a.start();

                    a = animationBuilder()
                            .autoReverse(true)
                            .interpolator(Interpolators.BACK.EASE_OUT())
                            .repeatInfinitely()
                            .duration(Duration.seconds(1.0))
                            .scale(enemy)
                            .from(new Point2D(1, 1))
                            .to(new Point2D(0, 0))
                            .build();

                    animations.add(a);
                    a.start();
                }, Duration.seconds(t));

                t += 0.25;
            }
        }
    }

    @Override
    public void onUpdate(double tpf) {
        animations.forEach(a -> a.onUpdate(tpf));
    }

    @Override
    public void destroy() {
        animations.forEach(Animation::stop);
    }
}
