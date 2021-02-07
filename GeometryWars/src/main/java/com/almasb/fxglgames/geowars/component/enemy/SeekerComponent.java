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

package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SeekerComponent extends Component {

    private Point2D velocity = Point2D.ZERO;
    private Entity player;
    private Entity seeker;

    private LocalTimer adjustDirectionTimer = FXGL.newLocalTimer();
    private Duration adjustDelay = Duration.seconds(0.15);

    private LocalTimer swapTexturesTimer = FXGL.newLocalTimer();
    private Duration swapTexturesDelay = Duration.seconds(0.2);

    private Texture overlay;

    private int moveSpeed;

    public SeekerComponent(Entity player, int moveSpeed) {
        this.player = player;
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded() {
        seeker = entity;
        adjustVelocity(0.016);

        overlay = FXGL.texture("Seeker_overlay.png", 60, 60).toColor(Color.WHITE);
    }

    @Override
    public void onUpdate(double tpf) {
        move(tpf);
        rotate();

        if (swapTexturesTimer.elapsed(swapTexturesDelay)) {
            swapTextures();
            swapTexturesTimer.capture();
        }
    }

    private void move(double tpf) {
        if (adjustDirectionTimer.elapsed(adjustDelay)) {
            adjustVelocity(tpf);
            adjustDirectionTimer.capture();
        }

        seeker.translate(velocity);
    }

    private void adjustVelocity(double tpf) {
        Point2D directionToPlayer = player.getCenter()
                .subtract(seeker.getCenter())
                .normalize()
                .multiply(moveSpeed);

        velocity = velocity.add(directionToPlayer).multiply(tpf);
    }

    private void rotate() {
        if (!velocity.equals(Point2D.ZERO)) {
            seeker.rotateToVector(velocity);
        }
    }

    private void swapTextures() {
        if (overlay.getScene() == null) {
            entity.getViewComponent().addChild(overlay);
        } else {
            entity.getViewComponent().removeChild(overlay);
        }
    }
}
