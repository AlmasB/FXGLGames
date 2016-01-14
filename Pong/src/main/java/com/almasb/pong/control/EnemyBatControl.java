/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.pong.control;

import com.almasb.fxgl.entity.Entity;
import com.almasb.pong.EntityTypes;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyBatControl extends BatControl {
    private Entity ball;

    @Override
    public void onUpdate(Entity entity) {
        super.onUpdate(entity);

        if (ball == null) {
            entity.getWorld()
                    .getEntities(EntityTypes.Type.BALL)
                    .stream()
                    .findAny()
                    .ifPresent(b -> ball = b);
        } else {
            moveAI();
        }
    }

    private void moveAI() {
        boolean isBallToLeft = ball.getRightX() <= entity.getX();

        if (ball.getY() < entity.getY()) {
            if (isBallToLeft)
                up();
            else
                down();
        } else if (ball.getBottomY() > entity.getBottomY()) {
            if (isBallToLeft)
                down();
            else
                up();
        } else {
            stop();
        }
    }
}
