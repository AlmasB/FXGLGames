package com.almasb.fxglgames.td.data;

import java.util.Comparator;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public record LevelData(
        String map,
        List<WaveData> waves
) {

    public int maxWaveIndex() {
        return waves()
                .stream()
                .max(Comparator.comparingInt(WaveData::index))
                .get()
                .index();
    }
}
