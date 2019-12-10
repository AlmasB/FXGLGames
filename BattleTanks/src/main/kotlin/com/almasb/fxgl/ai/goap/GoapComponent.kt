/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component
import java.util.*
import kotlin.collections.LinkedHashSet

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * This is the implementing class that provides our world data and listens to feedback on planning.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GoapComponent(
        private val worldProperties: PropertyMap,
        var goal: WorldState,
        actions: Set<GoapAction>) : Component() {

    /**
     * Idle is true when not performing any actions.
     */
    var isIdle = true
        private set

    var listener: GoapListener = NoopListener

    private val availableActions = LinkedHashSet(actions)
    private var currentActions: Queue<GoapAction> = ArrayDeque<GoapAction>()

    // TODO: add IdleAction?
    var currentAction: GoapAction? = null

    private fun hasActionPlan() = currentActions.isNotEmpty()

    override fun onAdded() {
        availableActions.forEach {
            if (it.isBound) {
                throw IllegalStateException("Action $it already bound to ${it.entity}")
            }

            it.entity = entity
        }
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
//        val worldState = listener.obtainWorldState(entity)
//        val goal = listener.createGoalState(entity)

        val worldState = WorldState(worldProperties)

        // add local state
        entity.properties.keys().forEach {
            worldState.add(it, entity.properties.getValue(it))
        }

        val plan = GoapPlanner.plan(availableActions, worldState, goal)

        if (!plan.isEmpty()) {
            currentActions = plan
            listener.planFound(entity, goal, plan)

            isIdle = false

        } else {
            listener.planFailed(entity, goal)

            isIdle = true
        }
    }

    private fun executePlan() {
        if (!hasActionPlan()) {
            isIdle = true
            listener.actionsFinished(entity)
            return
        }

        var action = currentActions.peek()
        if (action.isDone) {
            // the action is done. Remove it so we can perform the next one
            currentActions.remove()

            // re-run planning in case the world state has changed
            //makePlan()
        }

        if (hasActionPlan()) {
            // get the next action
            action = currentActions.peek()

            currentAction = action

            if (action is MoveGoapAction) {

                if (action.isInRange) {
                    // perform the action
                    val success = action.perform()

                    if (!success) {
                        // action failed, we need to plan again
                        isIdle = true
                        listener.planAborted(entity, action)
                    }
                } else {
                    // move
                    val success = action.move()

                    if (!success) {
                        // action failed, we need to plan again
                        isIdle = true
                        listener.planAborted(entity, action)
                    }
                }



            } else {
                // perform the action
                val success = action.perform()

                if (!success) {
                    // action failed, we need to plan again
                    isIdle = true
                    listener.planAborted(entity, action)
                }
            }

        } else {
            isIdle = true
            listener.actionsFinished(entity)
        }
    }

    fun addAction(action: GoapAction) {
        availableActions.add(action)
    }

    fun removeAction(action: GoapAction) {
        availableActions.remove(action)
    }

    private object NoopListener : GoapListener {
        override fun planFound(entity: Entity, goal: WorldState, actions: Queue<GoapAction>) {
        }

        override fun planFailed(entity: Entity, failedGoal: WorldState) {
        }

        override fun actionsFinished(entity: Entity) {
        }

        override fun planAborted(entity: Entity, aborter: GoapAction) {
        }
    }
}
