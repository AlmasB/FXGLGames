package com.almasb.geowars;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GeoWarsConfig {

    private double redEnemyChance;
    private int redEnemyHealth;
    private int redEnemyMoveSpeed;
    private int seekerMaxMoveSpeed;
    private int wandererMaxMoveSpeed;

    private int enemyHealth;

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

    public int getEnemyHealth() {
        return enemyHealth;
    }
}
