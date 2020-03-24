package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.scene.shape.QuadCurve;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level8 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                Entity enemy = spawnEnemy(50, 50 + y*50);

                QuadCurve path = new QuadCurve(50, 50 + y*50, 250, 200 + y * 20, Config.WIDTH - 50 - 40, 50 + y*50);

                Animation<?> anim = animationBuilder()
                        .delay(Duration.seconds(t))
                        .autoReverse(true)
                        .interpolator(Interpolators.BACK.EASE_IN_OUT())
                        .duration(Duration.seconds(3))
                        .repeat(Integer.MAX_VALUE)
                        .translate(enemy)
                        .alongPath(path)
                        .build();

                animations.add(anim);
                anim.start();

                t += 0.3;
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
