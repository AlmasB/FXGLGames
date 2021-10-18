package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum GeoWarsType {
    PLAYER,

    // enemies
    WANDERER, SEEKER, RUNNER, BOUNCER, BOMBER,

    // misc
    BULLET, CRYSTAL, SHOCKWAVE_PICKUP, MINE,

    // rendering helpers
    GRID, PARTICLE_LAYER,
    EXPLOSION, SHOCKWAVE
}
