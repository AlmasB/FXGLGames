package com.almasb.fxglgames.mario;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Component {

    private PhysicsComponent physics;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;

    public PlayerControl() {

        Image image = FXGL.image("player.png");

        animIdle = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        // TODO: fix scale origin
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.setView(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMoving()) {
            if (texture.getAnimationChannel() != animWalk) {
                texture.loopAnimationChannel(animWalk);
            }
        } else {
            if (texture.getAnimationChannel() != animIdle) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    private boolean isMoving() {
        return FXGLMath.abs(physics.getVelocityX()) > 0;
    }

    public void left() {
        getEntity().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        getEntity().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void jump() {
        physics.setVelocityY(-300);
    }
}
