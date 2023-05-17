package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum GeoWarsType {
    PLAYER,

    // enemies
    WANDERER, SEEKER, RUNNER, BOUNCER, BOMBER, BOSS,

    // pick-ups
    PICKUP_CRYSTAL, PICKUP_RICOCHET,

    // misc
    BULLET, SHOCKWAVE_PICKUP, MINE,

    // rendering helpers
    GRID, PARTICLE_LAYER,
    EXPLOSION, SHOCKWAVE
}
