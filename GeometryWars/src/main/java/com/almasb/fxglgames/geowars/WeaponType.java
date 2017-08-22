package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum WeaponType {
    SINGLE,
    DOUBLE,
    TRIPLE,
    RICOCHET,
    MIRROR;

    public boolean isBetterThan(WeaponType other) {
        return this.ordinal() > other.ordinal();
    }

    public static WeaponType fromMultiplier(int multiplier) {
        if (multiplier > 200) return MIRROR;
        if (multiplier > 100) return RICOCHET;
        if (multiplier > 50) return TRIPLE;
        if (multiplier > 25) return DOUBLE;

        return SINGLE;
    }
}
