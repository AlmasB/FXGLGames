package com.almasb.fxglgames.td.data;

/**
 * @param index wave number, all waves with same index get spawned at the same time
 * @param enemy entity type to spawn
 * @param amount number of enemies to spawn
 * @param way the way to use for enemies
 * @param reward how much money is given after this wave is cleared
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public record WaveData(
        int index,
        String enemy,
        int amount,
        String way,
        int reward
) { }
