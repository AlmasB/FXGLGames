package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.geti;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level9 extends SpaceLevel {

    @Override
    public void init() {
        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                Entity enemy = spawnEnemy(x*60, 150 + 50 * geti("level") + y*60);
                enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, Config.WIDTH, Config.HEIGHT / 2), FXGLMath.random(25, 75)));
            }
        }
    }

    @Override
    public void destroy() {

    }
}
