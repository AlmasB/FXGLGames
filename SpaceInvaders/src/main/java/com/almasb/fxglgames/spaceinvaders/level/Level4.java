package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level4 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                Entity enemy = spawnEnemy(50, 50);

                var path = new Rectangle(Config.WIDTH - 100, Config.HEIGHT / 2.0);
                path.setX(50);
                path.setY(50);

                var inter = new Rectangle(Config.WIDTH - 100, Config.HEIGHT);
                inter.setX(50);
                inter.setY(50);

                var a = animationBuilder()
                        .repeatInfinitely()
                        .delay(Duration.seconds(t))
                        .duration(Duration.seconds(2.5))
                        .translate(enemy)
                        // TODO: remove intersection
                        .alongPath(Shape.intersect(inter, path))
                        .build();

                animations.add(a);
                a.start();

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
