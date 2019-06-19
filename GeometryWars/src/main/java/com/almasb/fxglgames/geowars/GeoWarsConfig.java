package com.almasb.fxglgames.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GeoWarsConfig {

    private double redEnemyChance= 0.25;
    private int redEnemyHealth= 3;
    private int redEnemyMoveSpeed= 300;
    private int seekerMaxMoveSpeed= 250;
    private int wandererMaxMoveSpeed= 150;
    private int runnerMoveSpeed= 350;
    private int bouncerMoveSpeed= 500;

    private int playerSpeed= 350;

    private int enemyHealth= 1;

    public double getRedEnemyChance() {
        return redEnemyChance;
    }

    public int getRedEnemyHealth() {
        return redEnemyHealth;
    }

    public int getRedEnemyMoveSpeed() {
        return redEnemyMoveSpeed;
    }

    public int getSeekerMaxMoveSpeed() {
        return seekerMaxMoveSpeed;
    }

    public int getWandererMaxMoveSpeed() {
        return wandererMaxMoveSpeed;
    }

    public int getRunnerMoveSpeed() {
        return runnerMoveSpeed;
    }

    public int getBouncerMoveSpeed() {
        return bouncerMoveSpeed;
    }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }
}
