/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.BehaviorTreeContinuousAction;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveTask extends BehaviorTreeContinuousAction {

    @Override
    public boolean isCompleted() {
        return getEntity().getPosition().distance(400, 300) < 25;
    }

    @Override
    public void perform(double tpf) {

        double speed = tpf * 60 * 5;

        Point2D vector = new Point2D(400, 300).subtract(getEntity().getPosition())
                .normalize()
                .multiply(speed);

        getEntity().translate(vector);
    }
}
