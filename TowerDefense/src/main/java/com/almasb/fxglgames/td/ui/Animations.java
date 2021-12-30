package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Animations {

    public static void playWaveIconAnimation(WaveIcon icon) {
        animationBuilder()
                .duration(Duration.seconds(1.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .repeat(2)
                .autoReverse(true)
                .translate(icon)
                .from(new Point2D(10, 250))
                .to(new Point2D(100, 250))
                .buildAndPlay();
    }

    public static void playVisualEffectSlowAnimation(Entity visualEffect) {
        animationBuilder()
                .onFinished(() ->
                        visualEffect.addComponent(new ExpireCleanComponent(Duration.seconds(0.5)).animateOpacity())
                )
                .duration(Duration.seconds(0.75))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(visualEffect)
                .from(visualEffect.getPosition().add(-10, 0))
                .to(visualEffect.getPosition().add(-10, 20))
                .buildAndPlay();
    }
}
