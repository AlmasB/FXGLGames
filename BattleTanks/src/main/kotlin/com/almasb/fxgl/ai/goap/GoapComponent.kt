/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.action.ActionComponent
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.Required
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
@Required(ActionComponent::class)
class GoapComponent(
        private val worldProperties: PropertyMap,
        var goal: WorldState,
        actions: Set<GoapAction>) : Component() {

    private lateinit var actionComponent: ActionComponent

    var listener: GoapListener = NoopListener

    private val availableActions = LinkedHashSet(actions)

    override fun onAdded() {
        availableActions.forEach { it.entity = entity }
    }

    override fun onUpdate(tpf: Double) {
        if (actionComponent.isIdle) {
            makePlan()
        }
    }

    private fun makePlan() {
        // get the world state and the goal we want to plan for
        val worldState = WorldState(worldProperties)

        // add local state
        entity.properties.keys().forEach {
            worldState.add(it, entity.properties.getValue(it))
        }

        val plan = GoapPlanner.plan(availableActions, worldState, goal)

        if (!plan.isEmpty()) {
            listener.planFound(entity, goal, plan)

            plan.forEach { actionComponent.pushAction(it) }

        } else {
            listener.planFailed(entity, goal)
        }
    }








//
//    private fun executePlan() {
//        if (!hasActionPlan()) {
//            listener.actionsFinished(entity)
//            return
//        }
//
//        var action = currentActions.peek()
//        if (action.isComplete) {
//            // the action is done. Remove it so we can perform the next one
//            currentActions.remove()
//
//            // re-run planning in case the world state has changed
//            //makePlan()
//        }
//
//        if (hasActionPlan()) {
//            // get the next action
//            action = currentActions.peek()
//
//            currentAction = action
//
//            if (action is MoveGoapAction) {
//
//                if (action.isInRange) {
//                    // perform the action
//                    val success = action.perform()
//
//                    if (!success) {
//                        // action failed, we need to plan again
//                        isIdle = true
//                        listener.planAborted(entity, action)
//                    }
//                } else {
//                    // move
//                    val success = action.move()
//
//                    if (!success) {
//                        // action failed, we need to plan again
//                        isIdle = true
//                        listener.planAborted(entity, action)
//                    }
//                }
//
//
//
//            } else {
//                // perform the action
//                val success = action.perform()
//
//                if (!success) {
//                    // action failed, we need to plan again
//                    isIdle = true
//                    listener.planAborted(entity, action)
//                }
//            }
//
//        } else {
//            isIdle = true
//            listener.actionsFinished(entity)
//        }
//    }

//    fun addAction(action: GoapAction) {
//        availableActions.add(action)
//    }
//
//    fun removeAction(action: GoapAction) {
//        availableActions.remove(action)
//    }

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
