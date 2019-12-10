/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.entity.Entity

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class GoapAction {

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
    var cost = 1f

    /**
     * The entity performing this action.
     */
    lateinit var entity: Entity

    /**
     * An action often has to perform on an object.
     * This is that object. Can be null.
     */
    var target: Entity? = null

    /**
     * An action is bound if it was assigned to a GoapComponent.
     */
    internal val isBound: Boolean
        get() = this::entity.isInitialized

    /**
     * Is the action done?
     */
    abstract val isDone: Boolean

    /**
     * Check if this action can run.
     */
    open fun canRun() = true

    /**
     * Run the action.
     * Returns True if the action performed successfully or false
     * if something happened and it can no longer perform. In this case
     * the action queue should clear out and the goal cannot be reached.
     */
    abstract fun perform(): Boolean

    /**
     * Reset any variables that need to be reset before planning happens again.
     */
    open fun reset() { }

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

    override fun toString(): String {
        return javaClass.simpleName
    }

//    fun doReset() {
//        isInRange = false
//        target = null
//        reset()
//    }
}
