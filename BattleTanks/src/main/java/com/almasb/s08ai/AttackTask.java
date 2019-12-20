/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.AIControl;
import com.almasb.fxgl.ai.btree.SingleAction;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends SingleAction {

    @Override
    public void onUpdate(double tpf) {
        Entity player = ((BehaviorSample) FXGL.getApp()).player;

        if (player.distance(getEntity()) < 100) {
            getEntity().getComponent(AIControl.class).setBubbleMessage("Attack");
        } else {
            getEntity().getComponent(AIControl.class).setBubbleMessage("Chase");
            double speed = tpf * 60 * 5;

            Point2D vector = player.getPosition()
                    .subtract(getEntity().getPosition())
                    .normalize()
                    .multiply(speed);

            getEntity().translate(vector);
        }
    }
}
