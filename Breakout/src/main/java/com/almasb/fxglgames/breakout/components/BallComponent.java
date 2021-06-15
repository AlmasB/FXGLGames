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
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.breakout.BreakoutType;
import com.almasb.fxglgames.breakout.PowerupType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BallComponent extends Component {

    private static final Color[] COLORS = new Color[] {
            Color.WHITE, Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN
    };

    private static final int BALL_MIN_SPEED = 400;
    private static final int BALL_SLOW_SPEED = 100;

    private PhysicsComponent physics;
    private EffectComponent effectComponent;

    private Texture[] textures = new Texture[COLORS.length];
    private int colorIndex = 0;

    private Texture original;

    private boolean checkVelocityLimit = false;

    private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public void onAdded() {
        original = (Texture) entity.getViewComponent().getChildren().get(0);

        for (int i = 0; i < COLORS.length; i++) {
            textures[i] = (i == 0) ? original.copy() : original.multiplyColor(COLORS[i]).brighter().brighter();
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (checkVelocityLimit) {
            limitVelocity();
        }

        //checkOffscreen();
    }

    private void limitVelocity() {
        // we don't want the ball to move too slow in X direction
        if (abs(physics.getVelocityX()) < BALL_MIN_SPEED) {
            var signX = signum(physics.getVelocityX());

            // if 0, then choose direction to the right
            if (signX == 0.0)
                signX = 1.0;

            physics.setVelocityX(signX * BALL_MIN_SPEED);
        }

        // we don't want the ball to move too slow in Y direction
        if (abs(physics.getVelocityY()) < BALL_MIN_SPEED) {
            var signY = signum(physics.getVelocityY());

            // if 0, then choose upwards direction
            if (signY == 0.0)
                signY = -1.0;

            physics.setVelocityY(signY * BALL_MIN_SPEED);
        }
    }

    public void changeColorToNext() {
        entity.getViewComponent().removeChild(textures[colorIndex]);

        colorIndex++;

        if (colorIndex == textures.length)
            colorIndex = 0;

        setColor(COLORS[colorIndex]);

        if (!FXGL.getSettings().isNative()) {
            // update emitter
            var emitter = entity.getComponent(ParticleComponent.class).getEmitter();
            emitter.setSourceImage(textures[colorIndex]);
        }

        entity.getViewComponent().addChild(textures[colorIndex]);
    }

    public Color getNextColor() {
        int nextIndex = colorIndex + 1;

        if (nextIndex == textures.length) {
            nextIndex = 0;
        }

        return COLORS[nextIndex];
    }

    public void release() {
        checkVelocityLimit = true;

        physics.setBodyLinearVelocity(new Vec2(5, 5));
    }

    public void applyPowerup(PowerupType type) {
        switch (type) {
            case GROW:
                applyGrow();
                break;

            case MULTISHOT:
                applyMultishot();
                break;

            case ZOMBIE:
                applyZombie();
                break;
        }
    }

    public void applyGrow() {
        effectComponent.startEffect(new GrowEffect());
    }

    public void onHit() {
        effectComponent.startEffect(new HighlightEffect());
    }

    public void applySlow() {
        effectComponent.startEffect(new SlowEffect());
    }

    public void applyMultishot() {
        int numBalls = 10;

        for (int i = 0; i < numBalls; i++) {
            Point2D dir = Vec2.fromAngle(i*360.0 / numBalls).toPoint2D();

            spawn("bulletBall", new SpawnData(entity.getX(), entity.getY()).put("dir", dir));
        }
    }

    private void applyZombie() {
        byType(BreakoutType.BRICK).stream()
                .filter(brick -> brick.getPropertyOptional("markedByZombie").isEmpty())
                .findAny()
                .ifPresent(brick -> {
                    brick.setProperty("markedByZombie", true);

                    var rect = new Rectangle(brick.getWidth(), brick.getHeight(), null);
                    rect.setStroke(Color.YELLOW);

                    brick.getViewComponent().addChild(rect);

                    spawn("zombie", brick.getPosition().subtract(65, 65));

                    runOnce(() -> {
                        if (brick.isActive())
                            brick.call("onHit");
                    }, Duration.seconds(1.0));
                });
    }

    // this is a hack:
    // we use a physics engine, so it is possible to push the ball through a wall to outside of the screen
    private void checkOffscreen() {
        if (getEntity().getBoundingBoxComponent().isOutside(getGameScene().getViewport().getVisibleArea())) {
            physics.overwritePosition(new Point2D(
                    getAppWidth() / 2,
                    getAppHeight() / 2
            ));
        }
    }

    public static class GrowEffect extends Effect {

        public GrowEffect() {
            super(Duration.seconds(3));
        }

        @Override
        public void onStart(Entity entity) {
            entity.setScaleX(0.3);
            entity.setScaleY(0.3);
        }

        @Override
        public void onEnd(Entity entity) {
            entity.setScaleX(0.1);
            entity.setScaleY(0.1);
        }
    }

    public class HighlightEffect extends Effect {

        public HighlightEffect() {
            super(Duration.seconds(0.05));
        }

        @Override
        public void onStart(Entity entity) {
            //entity.getViewComponent().addChild(yellow);
        }

        @Override
        public void onEnd(Entity entity) {
            //entity.getViewComponent().removeChild(yellow);
        }
    }

    public class SlowEffect extends Effect {

        public SlowEffect() {
            super(Duration.seconds(0.15));
        }

        @Override
        public void onStart(Entity entity) {
            checkVelocityLimit = false;
        }

        @Override
        public void onUpdate(Entity entity, double tpf) {
            var signX = signum(physics.getVelocityX());
            var signY = signum(physics.getVelocityY());

            physics.setLinearVelocity(signX * BALL_SLOW_SPEED, signY * BALL_SLOW_SPEED);
        }

        @Override
        public void onEnd(Entity entity) {
            checkVelocityLimit = true;
        }
    }
}
