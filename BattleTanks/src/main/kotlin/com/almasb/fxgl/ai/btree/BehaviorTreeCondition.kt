/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree

import com.almasb.fxgl.entity.Entity

/**
 * Represents a single conditional statement of a behavior tree.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class BehaviorTreeCondition : LeafTask<Entity>() {

    /**
     * Condition succeeds if this returns true.
     */
    abstract fun evaluate(tpf: Double): Boolean

    override final fun execute(tpf: Double): Status {
        return if (evaluate(tpf)) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<Entity>): Task<Entity> {
        return task
    }
}