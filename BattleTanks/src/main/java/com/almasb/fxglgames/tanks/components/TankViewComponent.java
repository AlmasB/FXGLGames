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

package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.tanks.Config;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGL.texture;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TankViewComponent extends Component {

    private MoveComponent moveComponent;
    private ViewComponent view;

    private Texture texture;

    private double frameWidth;
    private double frameHeight;

    private LocalTimer shootTimer = FXGL.newLocalTimer();

    @Override
    public void onAdded() {
        texture = texture("player.png").multiplyColor(Color.LIGHTBLUE);
        view.addChild(texture);

        // there are 8 frames
        frameWidth = texture.getWidth() / 8;
        frameHeight = texture.getHeight();
    }

    private double speed = 0;
    private int frames = 0;

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * 60;

        int frame = frames / 10;
        if (frame >= 8) {
            frame = 0;
            frames = 0;
        }

        texture.setViewport(new Rectangle2D(frame * frameHeight, 0, frameWidth, frameHeight));
    }

    public void up() {

        moveComponent.setMoveDirection(MoveDirection.UP);
        frames++;
    }

    public void down() {

        moveComponent.setMoveDirection(MoveDirection.DOWN);
        frames++;
    }

    public void left() {

        moveComponent.setMoveDirection(MoveDirection.LEFT);
        frames++;
    }

    public void right() {

        moveComponent.setMoveDirection(MoveDirection.RIGHT);
        frames++;
    }

    public void shoot() {
        if (!shootTimer.elapsed(Config.SHOOT_DELAY))
            return;

        spawn("Bullet", new SpawnData(getEntity().getCenter())
                .put("direction", angleToVector())
                .put("owner", entity)
        );

        shootTimer.capture();
    }

    private Point2D angleToVector() {
        double angle = getEntity().getRotation();
        if (angle == 0) {
            return new Point2D(1, 0);
        } else if (angle == 90) {
            return new Point2D(0, 1);
        } else if (angle == 180) {
            return new Point2D(-1, 0);
        } else {    // 270
            return new Point2D(0, -1);
        }
    }
}
