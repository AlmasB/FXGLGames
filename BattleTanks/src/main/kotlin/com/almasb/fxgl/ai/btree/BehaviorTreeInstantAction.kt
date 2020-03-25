/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity

/**
 * An instant action that is executed in a single frame and it always succeeds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class BehaviorTreeInstantAction : LeafTask<Entity>() {

    abstract fun performOnce(tpf: Double)
    
    override final fun execute(tpf: Double): Status {
        performOnce(tpf)

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>): Task<Entity> {
        return task
    }
}