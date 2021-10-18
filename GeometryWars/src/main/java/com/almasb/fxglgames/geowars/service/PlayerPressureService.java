package com.almasb.fxglgames.geowars.service;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import static com.almasb.fxgl.dsl.FXGL.byType;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxglgames.geowars.Config.MAX_ENEMIES_PRESSURE;
import static com.almasb.fxglgames.geowars.GeoWarsType.*;
import static java.lang.Math.min;

/**
 * Provides dynamic difficulty gameplay by controlling how many enemies are being spawned,
 * based on the "pressure" factor.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerPressureService extends EngineService {

    /**
     * In range [0..1], where
     * 0 means no pressure (few enemies) and
     * 1 means max pressure (a lot of enemies).
     */
    private ReadOnlyDoubleWrapper pressureProp = new ReadOnlyDoubleWrapper();

    private boolean isSpawningEnemies = true;
    private boolean isOnCooldown = false;

    public double getPressure() {
        return pressureProp.get();
    }

    public ReadOnlyDoubleProperty pressurePropProperty() {
        return pressureProp.getReadOnlyProperty();
    }

    public boolean isSpawningEnemies() {
        return isSpawningEnemies;
    }

    @Override
    public void onGameUpdate(double tpf) {
        // not quite correct since this includes pick ups and other entities
        // but should be sufficient
        var numEntities = (getGameWorld().getEntities().size() - byType(BOMBER, CRYSTAL).size()) * 1.0;

        var value = min(numEntities / MAX_ENEMIES_PRESSURE, 1.0);

        pressureProp.setValue(value);

        if (isOnCooldown && value < 0.25) {
            isOnCooldown = false;
            isSpawningEnemies = true;
        }

        if (value == 1.0) {
            isOnCooldown = true;
            isSpawningEnemies = false;
        }
    }
}
