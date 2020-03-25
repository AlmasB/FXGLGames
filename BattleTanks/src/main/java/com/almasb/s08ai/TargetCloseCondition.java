/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.s08ai;

import com.almasb.fxgl.ai.btree.BehaviorTreeCondition;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TargetCloseCondition extends BehaviorTreeCondition {

    @Override
    public boolean evaluate(double tpf) {
        return new Point2D(400, 300).distance(getEntity().getPosition()) < 400;
    }
}
