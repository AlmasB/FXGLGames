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

    public static final boolean IS_MENU = true;

    public static final boolean IS_BACKGROUND = true;

    public static final boolean IS_SOUND_ENABLED = true;

    /**
     * How much space is between edges of the game and boundary of the playable grid.
     */
    public static final int OUTSIDE_DISTANCE = 200;

    /**
     * Max score that can be achieved.
     */
    public static final int GAME_OVER_SCORE = 10_000_000;

    public static final int ENEMY_HP = 1;

    public static final int MAX_ENEMIES_PRESSURE = 150;

    /* SPAWN INTERVALS FOR ENTITIES BEGIN */

    public static final Duration WANDERER_SPAWN_INTERVAL = seconds(1.5);
    public static final Duration SEEKER_SPAWN_INTERVAL = seconds(2.5);
    public static final Duration RUNNER_SPAWN_INTERVAL = seconds(3.5);
    public static final Duration BOUNCER_SPAWN_INTERVAL = seconds(4.5);
    public static final Duration WAVE_SPAWN_INTERVAL = seconds(50);

    public static final Duration MINE_SPAWN_INTERVAL = seconds(10);

    /* SPAWN INTERVALS FOR ENTITIES END */

    /* MOVE SPEED FOR ENTITIES BEGIN */

    public static final int WANDERER_MIN_MOVE_SPEED = 150;
    public static final int WANDERER_MAX_MOVE_SPEED = 180;

    public static final int SEEKER_MIN_MOVE_SPEED = 200;
    public static final int SEEKER_MAX_MOVE_SPEED = 250;

    public static final int RUNNER_MOVE_SPEED = 350;
    public static final int BOUNCER_MOVE_SPEED = 500;
    public static final int BOMBER_MOVE_SPEED = 150;

    public static final int PLAYER_SPEED = 320;

    public static final int BULLET_MOVE_SPEED = 1200;

    /* MOVE SPEED FOR ENTITIES END */

    public static final Duration WEAPON_DELAY = seconds(0.11);

    public static final String SAVE_FILE_NAME = "high_score.dat";
}
