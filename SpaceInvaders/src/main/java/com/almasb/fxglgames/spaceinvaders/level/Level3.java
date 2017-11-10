package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level3 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                final int finalY = y;

                FXGL.getMasterTimer().runOnceAfter(() -> {
                    boolean toRight = FXGLMath.randomBoolean();

                    Entity enemy = spawnEnemy(toRight ? 50 : Config.WIDTH - 50 - 40, 50 + finalY*75);

                    Animation<?> anim = Entities.animationBuilder()
                            .autoReverse(true)
                            .interpolator(Interpolators.SMOOTH.EASE_OUT())
                            .duration(Duration.seconds(3))
                            .repeat(Integer.MAX_VALUE)
                            .translate(enemy)
                            .from(enemy.getPosition())
                            .to(new Point2D(toRight ? Config.WIDTH - 50 - 40 : 50, 50 + finalY*75))
                            .buildAndPlay();

                    animations.add(anim);

                }, Duration.seconds(t));

                t += 0.3;
            }
        }
    }

    @Override
    public void destroy() {
        animations.forEach(Animation::stop);
    }
}
