/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.Condition;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CanSeePlayerCondition extends Condition {

    @Override
    public boolean evaluate() {
        Entity player = ((BehaviorSample) FXGL.getApp()).player;

        return player.distance(getEntity()) < 250;
    }
}
