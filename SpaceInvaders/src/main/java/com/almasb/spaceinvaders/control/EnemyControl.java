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

package com.almasb.spaceinvaders.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.spaceinvaders.EntityFactory;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {
    private LocalTimer hTimer;
    private LocalTimer vTimer;
    private LocalTimer attackTimer;
    private Duration nextAttack = Duration.seconds(2);

    private boolean movingRight = true;

    private AudioPlayer audioPlayer;

    @Override
    public void onAdded(Entity entity) {
        audioPlayer = GameApplication.getService(ServiceType.AUDIO_PLAYER);

        hTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        vTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        attackTimer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        hTimer.capture();
        vTimer.capture();
        attackTimer.capture();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        PositionComponent positionComponent = entity.getComponentUnsafe(PositionComponent.class);

        if (hTimer.elapsed(Duration.seconds(2))) {
            movingRight = !movingRight;
            hTimer.capture();
        }

        if (vTimer.elapsed(Duration.seconds(6))) {
            positionComponent.translateY(20);
            vTimer.capture();
        }

        if (attackTimer.elapsed(nextAttack)) {
            if (Math.random() < 0.3) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        positionComponent.translateX(movingRight ? 1 : -1);
    }

    private void shoot() {
        Entity bullet = EntityFactory.newBullet(getEntity());

        getEntity().getWorld().addEntity(bullet);

        audioPlayer.playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}
