package com.almasb.fxglgames.spacerunner.level;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level {

    private int currentWave = 0;

    public Level() {

    }

    public boolean isDone() {
        return currentWave == 1;
    }

    public void nextWave() {
        currentWave++;
    }

    public void spawnNewWave() {
        currentWave++;

        int numEnemies = 10;
        String[] enemyTypes = { "Enemy1" };

        // TODO: getSingletonOptional
        Entity player = FXGL.getApp().getGameWorld().getSingleton(SpaceRunnerType.PLAYER).get();

        for (int i = 0; i < numEnemies; i++) {
            String type = FXGLMath.random(enemyTypes).get();

            Entity e = FXGL.getApp().getGameWorld().spawn(type, new SpawnData(player.getX() + 700, FXGLMath.random(0, 400)));
        }
    }
}
