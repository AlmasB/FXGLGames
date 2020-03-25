/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.BehaviorTreeInstantAction;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends BehaviorTreeInstantAction {

    @Override
    public void performOnce(double tpf) {
        Entity player = ((BehaviorSample) FXGL.getApp()).player;

        if (player.distance(getEntity()) < 100) {
            //getEntity().getComponent(BehaviorTreeComponent.class).setBubbleMessage("Attack");
        } else {
            //getEntity().getComponent(BehaviorTreeComponent.class).setBubbleMessage("Chase");
            double speed = tpf * 60 * 5;

            Point2D vector = player.getPosition()
                    .subtract(getEntity().getPosition())
                    .normalize()
                    .multiply(speed);

            getEntity().translate(vector);
        }
    }
}
