package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RedLaserComponent extends Component {

    private CollidableComponent collidable;

    private int numHits;

    private boolean isMoving = false;

    public RedLaserComponent(int numHits) {
        this.numHits = numHits;
    }

    @Override
    public void onUpdate(double tpf) {
        if (isMoving)
            return;

        getGameWorld().getRandom(SpaceInvadersType.ENEMY).ifPresent(enemy -> {
            var vector = enemy.getPosition().subtract(entity.getPosition());

            playMoveAnimation(vector);
        });
    }

    private void playMoveAnimation(Point2D vector) {
        isMoving = true;

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .duration(Duration.seconds(0.6))
                .onFinished(() -> {
                    isMoving = false;
                    numHits--;

                    // need to check entity since it is possible that it is removed before
                    // animation is finished
                    if (numHits == 0 && entity != null) {
                        entity.removeFromWorld();
                    }
                })
                .translate(entity)
                .from(entity.getPosition())
                .to(entity.getPosition().add(vector.multiply(1.2)))
                .buildAndPlay();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                .autoReverse(true)
                .repeat(2)
                .duration(Duration.seconds(0.6))
                .scale(entity)
                .from(new Point2D(1, 1))
                .to(new Point2D(5, 0.2))
                .buildAndPlay();
    }
}
