/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.BehaviorTreeCondition;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CanSeePlayerCondition extends BehaviorTreeCondition {

    @Override
    public boolean evaluate(double tpf) {
        Entity player = ((BehaviorSample) FXGL.getApp()).player;

        return player.distance(getEntity()) < 250;
    }
}
