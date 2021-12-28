package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.animation.Interpolators;
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
}
