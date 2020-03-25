/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree

import com.almasb.fxgl.ai.btree.utils.BehaviorTreeParser
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component

/**
 * Allows attaching a behavior tree to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BehaviorTreeComponent
private constructor() : Component() {

    companion object {
        private val parsedTreesCache = hashMapOf<String, BehaviorTree<Entity> >()
    }

    private lateinit var behaviorTree: BehaviorTree<Entity>

    /**
     * Constructs AI control with given [behaviorTree].
     */
    constructor(behaviorTree: BehaviorTree<Entity>) : this() {
        this.behaviorTree = behaviorTree
    }

    /**
     * Constructs AI control with behavior tree parsed from the asset with name [treeName].
     */
    constructor(treeName: String) : this() {

        var tree = parsedTreesCache[treeName]

        if (tree == null) {

            tree = BehaviorTreeParser<Entity>().parse(FXGL.getAssetLoader().getStream("/assets/ai/$treeName"), null)
            //tree = FXGL.getAssetLoader().loadBehaviorTree(treeName)
            parsedTreesCache[treeName] = tree
        }

        this.behaviorTree = tree!!.cloneTask() as BehaviorTree<Entity>
    }

    override fun onAdded() {
        behaviorTree.entity = entity
    }

    override fun onUpdate(tpf: Double) {
        behaviorTree.step(tpf)
    }
}