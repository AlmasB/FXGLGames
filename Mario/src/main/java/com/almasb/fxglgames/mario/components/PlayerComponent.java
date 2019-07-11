package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxglgames.mario.MarioApp;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.image;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(HPComponent.class)
public class PlayerComponent extends Component {

    private PhysicsComponent physics;
    private HPComponent hp;

    private AnimatedTexture texture;

    private AnimationChannel animIdle, animWalk;

    private int jumps = 2;

    private boolean isBeingDamaged = false;

    public PlayerComponent() {

        Image image = image("player.png");

        animIdle = new AnimationChannel(image, 4, 32, 42, Duration.seconds(1), 1, 1);
        animWalk = new AnimationChannel(image, 4, 32, 42, Duration.seconds(0.66), 0, 3);

        texture = new AnimatedTexture(animIdle);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(16, 21));
        entity.setView(texture);

        physics.onGroundProperty().addListener((obs, old, isOnGround) -> {
            if (isOnGround) {
                //play("land.wav");
                jumps = 2;
            }
        });
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
        return physics.isMovingX();
    }

    public void left() {
        if (isBeingDamaged)
            return;

        getEntity().setScaleX(-1);
        physics.setVelocityX(-170);
    }

    public void right() {
        if (isBeingDamaged)
            return;

        getEntity().setScaleX(1);
        physics.setVelocityX(170);
    }

    public void stop() {
        if (isBeingDamaged)
            return;

        physics.setVelocityX(0);
    }

    public void jump() {
        if (isBeingDamaged)
            return;

        if (jumps == 0)
            return;

        //play("jump.wav");
        physics.setVelocityY(-300);

        jumps--;
    }

    public void superJump() {
        physics.setVelocityY(-930);
    }

    public void superJump(Point2D vector) {
        physics.setLinearVelocity(vector);

        isBeingDamaged = true;

        // can't control player for 2 seconds
        runOnce(() -> {
            isBeingDamaged = false;
        }, Duration.seconds(2));
    }

    public void onHit(Entity attacker) {
        if (isBeingDamaged)
            return;

        if (hp.getValue() == 0)
            return;

        hp.setValue(hp.getValue() - 10);

        Point2D dmgVector = entity.getPosition().subtract(attacker.getPosition());

        isBeingDamaged = true;

        physics.setLinearVelocity(new Point2D(Math.signum(dmgVector.getX()) * 290, -300));

        // Damage time 1 sec
        runOnce(() -> {
            isBeingDamaged = false;
            physics.setVelocityX(0);
        }, Duration.seconds(1));

        if (hp.getValue() == 0) {
            FXGL.<MarioApp>getAppCast().onPlayerDied();
        }
    }

    public void restoreHP() {
        hp.setValue(hp.getMaxHP());
    }
}
