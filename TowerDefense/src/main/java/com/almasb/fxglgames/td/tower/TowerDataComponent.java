package com.almasb.fxglgames.td.tower;

import com.almasb.fxgl.entity.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDataComponent extends Component {

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
