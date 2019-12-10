package com.almasb.fxgl.ai.goap

/**
 * Close range goap action, i.e. entity needs to move
 * to reach target before executing the actual action.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class MoveGoapAction : GoapAction() {

    abstract val isInRange: Boolean

    abstract fun move(): Boolean
}