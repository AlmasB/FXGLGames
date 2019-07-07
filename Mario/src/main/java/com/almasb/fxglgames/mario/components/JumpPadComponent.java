package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JumpPadComponent extends Component {

    public void activate() {
        entity.getComponent(CollidableComponent.class).setValue(false);

        animationBuilder()
                .autoReverse(true)
                .repeat(2)
                .duration(Duration.seconds(0.15))
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .onFinished(() -> entity.getComponent(CollidableComponent.class).setValue(true))
                .translate(entity)
                .from(entity.getPosition())
                .to(entity.getPosition().subtract(0, 50))
                .buildAndPlay();
    }
}
