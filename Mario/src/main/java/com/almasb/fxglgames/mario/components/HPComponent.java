package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.entity.components.IntegerComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HPComponent extends IntegerComponent {

    private final int maxHP;

    public HPComponent(int hp) {
        super(hp);
        maxHP = hp;
    }

    public int getMaxHP() {
        return maxHP;
    }
}
