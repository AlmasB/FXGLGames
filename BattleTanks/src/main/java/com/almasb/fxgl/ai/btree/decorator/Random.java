/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.almasb.fxgl.ai.btree.decorator;

import com.almasb.fxgl.ai.btree.Decorator;
import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.ai.btree.annotation.TaskConstraint;
import com.almasb.fxgl.ai.btree.leaf.Failure;
import com.almasb.fxgl.ai.btree.leaf.Success;
import com.almasb.fxgl.core.math.FXGLMath;

/**
 * The {@code Random} decorator succeeds with the specified probability, regardless of whether the wrapped task fails or succeeds.
 * Also, the wrapped task is optional, meaning that this decorator can act like a leaf task.
 * <p>
 * Notice that if success probability is 1 this task is equivalent to the decorator {@link AlwaysSucceed} and the leaf
 * {@link Success}. Similarly if success probability is 0 this task is equivalent to the decorator {@link AlwaysFail} and the leaf
 * {@link Failure}.
 *
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * @author davebaol
 */
@TaskConstraint(minChildren = 0, maxChildren = 1)
public class Random<E> extends Decorator<E> {

    /**
     * Creates a {@code Random} decorator with no child that succeeds or fails with equal probability.
     */
    public Random() {
        super();
    }

    /**
     * Creates a {@code Random} decorator with the given child that succeeds with the specified probability.
     *
     * @param task    the child task to wrap
     */
    public Random(Task<E> task) {
        super(task);
    }

    /**
     * Draws a value from the distribution that determines the success probability.
     * <p>
     * This method is called when the task is entered.
     */
    @Override
    public void start() {
    }

    @Override
    public void run() {
        if (child != null)
            super.run();
        else
            decide();
    }

    @Override
    public void childFail(Task<E> runningTask) {
        decide();
    }

    @Override
    public void childSuccess(Task<E> runningTask) {
        decide();
    }

    private void decide() {
        if (FXGLMath.randomBoolean())
            success();
        else
            fail();
    }

    @Override
    protected Task<E> copyTo(Task<E> task) {
        Random<E> random = (Random<E>) task;

        return super.copyTo(task);
    }
}
