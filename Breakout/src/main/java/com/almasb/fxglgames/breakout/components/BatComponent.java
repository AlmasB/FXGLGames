/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.effect.Effect;
import javafx.scene.effect.MotionBlur;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BatComponent extends Component {

    private static final int BAT_SPEED = 1050;
    private static final float BOUNCE_FACTOR = 1.5f;
    private static final float SPEED_DECAY = 0.66f;

    private PhysicsComponent physics;
    private float speed = 0;

    private Vec2 velocity = new Vec2();

    private Effect blur = new MotionBlur();

    private Texture textureOnHit;

    public BatComponent(Texture textureOnHit) {
        this.textureOnHit = textureOnHit;
    }

    @Override
    public void onUpdate(double tpf) {
        speed = BAT_SPEED * (float)tpf;

        velocity.mulLocal(SPEED_DECAY);

        if (entity.getX() < 0) {
            velocity.set(BOUNCE_FACTOR * (float) -entity.getX(), 0);
        } else if (entity.getRightX() > getAppWidth()) {
            velocity.set(BOUNCE_FACTOR * (float) -(entity.getRightX() - getAppWidth()), 0);
        }

        physics.setBodyLinearVelocity(velocity);
    }

    public void left() {
        velocity.set(-speed, 0);
        applyMoveEffects();
    }

    public void right() {
        velocity.set(speed, 0);
        applyMoveEffects();
    }

    private void applyMoveEffects() {
        entity.setScaleX(1.05);
        entity.setScaleY(1 / entity.getScaleX());
        entity.getViewComponent().getParent().setEffect(blur);
    }

    public void stop() {
        entity.setScaleX(1);
        entity.setScaleY(1);
        entity.getViewComponent().getParent().setEffect(null);
    }

    public void onHit() {
        entity.getComponent(EffectComponent.class).startEffect(new HitEffect());
    }

    public class HitEffect extends com.almasb.fxgl.dsl.components.Effect {

        public HitEffect() {
            super(Duration.seconds(0.1));
        }

        @Override
        public void onStart(Entity entity) {
            entity.getViewComponent().addChild(textureOnHit);
        }

        @Override
        public void onEnd(Entity entity) {
            entity.getViewComponent().removeChild(textureOnHit);
        }
    }
}
