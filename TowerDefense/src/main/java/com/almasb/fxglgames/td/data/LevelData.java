package com.almasb.fxglgames.td.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public record LevelData(
        String name,
        String map,
        List<WaveData> waves,
        List<QuestData> quests
) {

    public int maxWaveIndex() {
        return waves()
                .stream()
                .max(Comparator.comparingInt(WaveData::index))
                .get()
                .index();
    }

    /**
     * @return all waves in this level with given index
     */
    public Stream<WaveData> waves(int index) {
        return waves()
                .stream()
                .filter(w -> w.index() == index);
    }
}
