package com.almasb.fxglgames.geowars;

import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class Config {

    public static final int OUTSIDE_DISTANCE = 200;

    public static final int ENEMY_HP = 1;

    public static final int WANDERER_MIN_MOVE_SPEED = 100;
    public static final int WANDERER_MAX_MOVE_SPEED = 150;

    public static final int SEEKER_MIN_MOVE_SPEED = 100;
    public static final int SEEKER_MAX_MOVE_SPEED = 150;

    public static final int RUNNER_MOVE_SPEED = 350;
    public static final int BOUNCER_MOVE_SPEED = 500;

    public static final int PLAYER_SPEED = 320;

    public static final Duration WEAPON_DELAY = Duration.seconds(0.11);

    public static final String SAVE_FILE_NAME = "high_score.dat";
}
