/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.entity.component.Component
import java.util.*

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * This is the implementing class that provides our world data and listens to feedback on planning.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GoapComponent(
        private val agent: GoapAgent,
        actions: Set<GoapAction>) : Component() {

    /**
     * Idle is true when not performing any actions.
     */
    var isIdle = true
        private set

    private val availableActions = HashSet(actions)
    private var currentActions: Queue<GoapAction> = ArrayDeque<GoapAction>()

    private fun hasActionPlan() = currentActions.isNotEmpty()

    override fun onAdded() {
        availableActions.forEach { it.entity = entity }
    }

    override fun onUpdate(tpf: Double) {
        if (isIdle) {
            makePlan()
        } else {
            executePlan()
        }
    }

    private fun makePlan() {
        // get the world state and the goal we want to plan for
        val worldState = agent.obtainWorldState(entity)
        val goal = agent.createGoalState(entity)

        val plan = GoapPlanner.plan(availableActions, worldState, goal)
        if (!plan.isEmpty()) {
            currentActions = plan
            agent.planFound(entity, goal, plan)

            isIdle = false

        } else {
            agent.planFailed(entity, goal)

            isIdle = true
        }
    }

    private fun executePlan() {
        if (!hasActionPlan()) {
            isIdle = true
            agent.actionsFinished(entity)
            return
        }

        var action = currentActions.peek()
        if (action.isDone) {
            // the action is done. Remove it so we can perform the next one
            currentActions.remove()
        }

        if (hasActionPlan()) {
            // get the next action
            action = currentActions.peek()

            // perform the action
            val success = action.perform()

            if (!success) {
                // action failed, we need to plan again
                isIdle = true
                agent.planAborted(entity, action)
            }

        } else {
            isIdle = true
            agent.actionsFinished(entity)
        }
    }

    fun addAction(action: GoapAction) {
        availableActions.add(action)
    }

    fun removeAction(action: GoapAction) {
        availableActions.remove(action)
    }
}
