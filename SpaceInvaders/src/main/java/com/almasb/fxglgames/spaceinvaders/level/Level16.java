package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.core.math.FXGLMath.cos;
import static com.almasb.fxgl.core.math.FXGLMath.sin;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameTimer;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level16 extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    private double t = 0.0;

    @Override
    public void init() {
        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                Entity enemy;

                if (y == 0) {
                    enemy = spawnEnemy(Config.WIDTH / 2, 50);

                    Animation<?> anim = animationBuilder()
                            .delay(Duration.seconds(t))
                            .autoReverse(true)
                            .duration(Duration.seconds(1))
                            .repeat(Integer.MAX_VALUE)
                            .translate(enemy)
                            .from(enemy.getPosition())
                            .to(new Point2D(Config.WIDTH - 50 - 40, Config.HEIGHT / 2 - 50))
                            .build();

                    animations.add(anim);
                    anim.start();

                    enemy = spawnEnemy(Config.WIDTH - 50 - 40, 50);

                    anim = animationBuilder()
                            .delay(Duration.seconds(t))
                            .autoReverse(true)
                            .duration(Duration.seconds(1))
                            .repeat(Integer.MAX_VALUE)
                            .translate(enemy)
                            .from(enemy.getPosition())
                            .to(new Point2D(Config.WIDTH / 2, Config.HEIGHT / 2 - 50))
                            .build();

                    animations.add(anim);
                    anim.start();

                } else {
                    getGameTimer().runOnceAfter(() -> {
                        var e = spawnEnemy(0, 0);
                        e.addComponent(new MoveComponent());
                    }, Duration.seconds(t));
                }

                t += 0.2;
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

    private static class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(curveFunction().add(0, FXGL.getAppHeight() / 2 - 300));

            t += tpf;
        }

        private Point2D curveFunction() {
            double x = Math.pow(2, cos(t));
            double y = 1.6 * Math.pow(3, sin(t)) - 1;

            return new Point2D(x, y).multiply(85);
        }
    }
}
