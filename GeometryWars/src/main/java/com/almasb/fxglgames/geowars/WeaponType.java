package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum WeaponType {
    SINGLE,
    DOUBLE,
    TRIPLE;

    public boolean isBetterThan(WeaponType other) {
        return this.ordinal() > other.ordinal();
    }

    public static WeaponType fromMultiplier(int multiplier) {
        if (multiplier > 200) return TRIPLE;
        if (multiplier > 100) return DOUBLE;

        return SINGLE;
    }
}
