/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity

/**
 * In a behavior tree a continuous action is executed until it is completed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class BehaviorTreeContinuousAction : LeafTask<Entity>() {

    /**
     * The action succeeds when this returns true.
     */
    abstract fun isCompleted(): Boolean

    /**
     * Executed every frame when action is active.
     */
    abstract fun perform(tpf: Double)

    override final fun execute(tpf: Double): Status {
        if (isCompleted())
            return Status.SUCCEEDED

        perform(tpf)
        return if (isCompleted()) Status.SUCCEEDED else Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>): Task<Entity> {
        return task
    }
}