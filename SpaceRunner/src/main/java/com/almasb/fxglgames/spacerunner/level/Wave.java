package com.almasb.fxglgames.spacerunner.level;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Wave {

    private int numEnemies;
    private List<Entity> enemies = new ArrayList<>();

    public Wave(int numEnemies, String... enemyTypes) {
        this.numEnemies = numEnemies;

        if (enemyTypes.length == 0) {
            throw new IllegalArgumentException("At least 1 type is required");
        }

        // TODO: getSingletonOptional
        Entity player = FXGL.getApp().getGameWorld().getSingleton(SpaceRunnerType.PLAYER).get();

        for (int i = 0; i < numEnemies; i++) {
            String type = FXGLMath.random(enemyTypes).get();

            Entity e = FXGL.getApp().getGameWorld().create(type, new SpawnData(player.getX() + 700, FXGLMath.random(0, 500)));

            enemies.add(e);
        }
    }

    public List<Entity> getEnemies() {
        return enemies;
    }

    public boolean isDone() {
        for (Entity e : enemies) {
            if (e.getComponent(HealthComponent.class).getValue() > 0) {
                return false;
            }
        }

        return true;
    }
}
