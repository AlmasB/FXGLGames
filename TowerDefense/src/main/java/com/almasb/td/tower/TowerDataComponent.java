package com.almasb.td.tower;

import com.almasb.fxgl.ecs.AbstractComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDataComponent extends AbstractComponent {

    private int hp;
    private int damage;
    private double attackDelay;

    public int getHP() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackDelay() {
        return attackDelay;
    }
}
