package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.SequentialAnimation;
import com.almasb.fxgl.app.FXGL;
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
public class Level4 extends SpaceLevel {

    private List<SequentialAnimation> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                FXGL.getMasterTimer().runOnceAfter(() -> {
                    Entity enemy = spawnEnemy(50, 50);

                    Animation<?> anim1 = Entities.animationBuilder()
                            .duration(Duration.seconds(2.5))
                            .translate(enemy)
                            .from(new Point2D(50, 50))
                            .to(new Point2D(Config.WIDTH - 50 - 40, 50))
                            .build();

                    Animation<?> anim2 = Entities.animationBuilder()
                            .duration(Duration.seconds(2.5))
                            .translate(enemy)
                            .from(new Point2D(Config.WIDTH - 50 - 40, 50))
                            .to(new Point2D(Config.WIDTH - 50 - 40, Config.HEIGHT / 2))
                            .build();

                    Animation<?> anim3 = Entities.animationBuilder()
                            .duration(Duration.seconds(2.5))
                            .translate(enemy)
                            .from(new Point2D(Config.WIDTH - 50 - 40, Config.HEIGHT / 2))
                            .to(new Point2D(50, Config.HEIGHT / 2))
                            .build();

                    Animation<?> anim4 = Entities.animationBuilder()
                            .duration(Duration.seconds(2.5))
                            .translate(enemy)
                            .from(new Point2D(50, Config.HEIGHT / 2))
                            .to(new Point2D(50, 50))
                            .build();

                    SequentialAnimation anim = new SequentialAnimation(anim1, anim2, anim3, anim4);
                    anim.setCycleCount(Integer.MAX_VALUE);
                    anim.startInPlayState();
                    animations.add(anim);

                }, Duration.seconds(t));

                t += 0.3;
            }
        }
    }

    @Override
    public void destroy() {
        animations.forEach(SequentialAnimation::stop);
    }
}
