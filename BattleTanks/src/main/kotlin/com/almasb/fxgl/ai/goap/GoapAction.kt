/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.action.Action

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class GoapAction : Action() {

    /**
     * Predicates that need to be true for this action to run.
     */
    val preconditions = WorldState()

    /**
     * Results that are true after this action successfully completed.
     */
    val effects = WorldState()

    /**
     * The cost of performing the action.
     * Actions with total lowest cost are chosen during planning.
     */
    open var cost = 1f

    /**
     * An action often has to perform on an object.
     * This is that object. Can be null.
     */
    var target: Entity? = null

    /**
     * Check if this action can run.
     * TODO: is available, rather than can run.
     */
    open fun canRun() = true

    override fun onUpdate(tpf: Double) {
        // TODO: perform(tpf)
        perform()
    }

    /**
     * Perform the action.
     */
    abstract fun perform()

    fun addPrecondition(key: String, value: Any) {
        preconditions.add(key, value)
    }

    fun removePrecondition(key: String) {
        preconditions.remove(key)
    }

    fun addEffect(key: String, value: Any) {
        effects.add(key, value)
    }

    fun removeEffect(key: String) {
        effects.remove(key)
    }
}
