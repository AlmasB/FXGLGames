package com.almasb.fxglgames.geowars;

import javafx.util.Duration;

import static javafx.util.Duration.*;

/**
 * Defines global constants that can be tweaked for fine-tuning gameplay.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class Config {

    public static final boolean IS_RELEASE = false;

    /**
     * Disables enemies if true, useful for quick level building / testing.
     */
    public static final boolean IS_NO_ENEMIES = false;

    public static final boolean IS_MENU = false;

    public static final boolean IS_BACKGROUND = true;

    public static final boolean IS_SOUND_ENABLED = false;

    public static final boolean IS_TIME_HP_PENALTY = true;

    /**
     * How much space is between edges of the game and boundary of the playable grid.
     */
    public static final int OUTSIDE_DISTANCE = 200;

    /**
     * Max score that can be achieved.
     */
    public static final int GAME_OVER_SCORE = 10_000_000;

    public static final int MAX_MULTIPLIER = 500;

    public static final int ENEMY_HP = 1;
    public static final int BOSS_HP = 10;

    public static final int MAX_ENEMIES_PRESSURE = 150;

    /* SPAWN INTERVALS FOR ENTITIES BEGIN */

    public static final Duration WANDERER_SPAWN_INTERVAL = seconds(1.5);
    public static final Duration SEEKER_SPAWN_INTERVAL = seconds(2.0);
    public static final Duration RUNNER_SPAWN_INTERVAL = seconds(3.5);
    public static final Duration BOUNCER_SPAWN_INTERVAL = seconds(4.5);
    public static final Duration WAVE_SPAWN_INTERVAL = seconds(50);

    public static final Duration MINE_SPAWN_INTERVAL = seconds(10);
    public static final Duration PICKUP_RICOCHET_SPAWN_INTERVAL = seconds(10);

    /* SPAWN INTERVALS FOR ENTITIES END */

    /* MOVE SPEED FOR ENTITIES BEGIN */

    public static final int WANDERER_MOVE_SPEED = 170;

    public static final int SEEKER_MOVE_SPEED = 380;

    public static final int RUNNER_MOVE_SPEED = 350;
    public static final int BOUNCER_MOVE_SPEED = 500;
    public static final int BOMBER_MOVE_SPEED = 150;

    public static final int PLAYER_SPEED = 480;

    public static final int BULLET_MOVE_SPEED = 1200;

    /* MOVE SPEED FOR ENTITIES END */

    public static final int BACKGROUND_Z_INDEX = -10;
    public static final int PARTICLES_Z_INDEX = -5;
    public static final int PLAYER_Z_INDEX = 10;
    public static final int ENEMIES_Z_INDEX = 50;
    public static final int BULLET_Z_INDEX = 30;
    public static final int PICKUP_Z_INDEX = 5;

    public static final Duration WEAPON_DELAY = seconds(0.11);

    public static final String SAVE_FILE_NAME = "high_score.dat";

    public static final int PLAYER_HP = 50;
    public static final int COLLISION_PENALTY = -20;
    public static final int TIME_PENALTY = -1;
    public static final Duration PENALTY_INTERVAL = seconds(0.5);

    public static final Duration PICKUP_RICOCHET_DURATION = seconds(3);

    public static final int MAX_CHARGES_SECONDARY = 30;
}
