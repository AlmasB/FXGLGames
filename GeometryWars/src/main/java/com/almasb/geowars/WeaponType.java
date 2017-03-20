package com.almasb.geowars;

import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum WeaponType {
    NORMAL(Duration.seconds(0.17)),
    RICOCHET(Duration.seconds(0.17 * 3)),
    BEAM(Duration.seconds(0.17 * 7)),
    WAVE(Duration.seconds(0.17 * 10));

    final Duration delay;

    WeaponType(Duration delay) {
        this.delay = delay;
    }
}
