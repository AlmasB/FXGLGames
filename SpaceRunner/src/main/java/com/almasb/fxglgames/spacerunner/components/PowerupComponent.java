package com.almasb.fxglgames.spacerunner.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.spacerunner.PowerupType;
import javafx.geometry.Point2D;
import javafx.util.Duration;


import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PowerupComponent extends Component {

    private PowerupType type;

    public PowerupComponent(PowerupType type) {
        this.type = type;
    }

    public PowerupType getType() {
        return type;
    }

    public void onCollisionBegin(Entity player) {
        play("pickup.wav");

        switch (type) {
            case BONUS_SCORE_SMALL:
                inc("score", +100);
                break;
            case BONUS_SCORE_MEDIUM:
                inc("score", +300);
                break;
            case BONUS_SCORE_HIGH:
                inc("score", +500);
                break;
            default:
                throw new IllegalArgumentException("Unknown powerup type: " + type);
        }

        entity.getComponent(CollidableComponent.class).setValue(false);

        animationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(() -> entity.removeFromWorld())
                .scale(entity)
                .to(new Point2D(1, 1))
                .to(Point2D.ZERO)
                .buildAndPlay();
    }
}
