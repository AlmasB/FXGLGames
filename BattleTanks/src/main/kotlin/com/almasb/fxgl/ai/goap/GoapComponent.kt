/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.entity.Entity
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
        private val moveSpeed: Double,
        actions: Set<GoapAction>) : Component() {

    private val stateMachine = FSM()

    /**
     * Thinking (finds something to do).
     */
    private val idleState: FSMState

    /**
     * Moving to target.
     */
    private val moveToState: FSMState

    /**
     * Performing an action.
     */
    private val performActionState: FSMState

    private val availableActions = HashSet(actions)
    private var currentActions: Queue<GoapAction> = ArrayDeque<GoapAction>()

    init {
        idleState = createIdleState()
        moveToState = createMoveToState()
        performActionState = createPerformActionState()

        stateMachine.pushState(idleState)
    }

    override fun onAdded() {
        availableActions.forEach { it.entity = entity }
    }

    private var tpf: Double = 0.0

    override fun onUpdate(tpf: Double) {
        this.tpf = tpf
        stateMachine.update(entity)
    }

    fun addAction(action: GoapAction) {
        availableActions.add(action)
    }

    fun removeAction(action: GoapAction) {
        availableActions.remove(action)
    }

    private fun hasActionPlan() = currentActions.isNotEmpty()

    /**
     * Called during update.
     * Move the agent towards the target in order
     * for the next action to be able to perform.
     * Return true if the Agent is at the target and the next action can perform.
     * False if it is not there yet.
     */
    private fun moveAgent(nextAction: GoapAction): Boolean {
        requireNotNull(nextAction.target) { "GoapAction: $nextAction has no target" }

        val targetPosition = nextAction.target!!.position

        val moveDistance = moveSpeed * tpf

        if (entity.position.distance(targetPosition) >= moveDistance) {
            entity.translate(targetPosition.subtract(entity.position).normalize().multiply(moveDistance))
            return false
        }

        nextAction.isInRange = true
        return true
    }

    private fun createIdleState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {
                // get the world state and the goal we want to plan for
                val worldState = agent.obtainWorldState(entity)
                val goal = agent.createGoalState(entity)

                val plan = GoapPlanner.plan(availableActions, worldState, goal)
                if (!plan.isEmpty()) {
                    currentActions = plan
                    agent.planFound(entity, goal, plan)

                    fsm.popState() // move to PerformAction state
                    fsm.pushState(performActionState)

                } else {
                    agent.planFailed(entity, goal)
                    fsm.popState() // move back to IdleAction state
                    fsm.pushState(idleState)
                }
            }
        }
    }

    private fun createMoveToState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {

                val action = currentActions.peek()
                if (action.requiresInRange() && action.target == null) {
                    fsm.popState() // move
                    fsm.popState() // perform
                    fsm.pushState(idleState)
                    return
                }

                // get the agent to move itself
                if (moveAgent(action)) {
                    fsm.popState()
                }
            }
        }
    }

    private fun createPerformActionState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {
                if (!hasActionPlan()) {
                    // no actions to perform
                    fsm.popState()
                    fsm.pushState(idleState)
                    agent.actionsFinished(entity)
                    return
                }

                var action = currentActions.peek()
                if (action.isDone) {
                    // the action is done. Remove it so we can perform the next one
                    currentActions.remove()
                }

                if (hasActionPlan()) {
                    // perform the next action
                    action = currentActions.peek()
                    val inRange = if (action.requiresInRange()) action.isInRange else true

                    if (inRange) {
                        // we are in range, so perform the action
                        val success = action.perform()

                        if (!success) {
                            // action failed, we need to plan again
                            fsm.popState()
                            fsm.pushState(idleState)
                            agent.planAborted(entity, action)
                        }
                    } else {
                        // we need to move there first
                        // push moveTo state
                        fsm.pushState(moveToState)
                    }

                } else {
                    // no actions left, move to Plan state
                    fsm.popState()
                    fsm.pushState(idleState)
                    agent.actionsFinished(entity)
                }
            }
        }
    }
}
