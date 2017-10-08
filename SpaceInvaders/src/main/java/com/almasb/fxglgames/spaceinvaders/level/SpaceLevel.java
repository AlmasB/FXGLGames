package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.spawn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class SpaceLevel {

    public abstract void init();
    public abstract void destroy();

    protected GameEntity spawnEnemy(double x, double y) {
        GameEntity enemy = (GameEntity) spawn("Enemy", x, y);

        Entities.animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(FXGLMath.random() * 2))
                .scale(enemy)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();

        return enemy;
    }
}
