package com.almasb.fxglgames.geowars.wave;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurve;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WaveFactory {

    private static final double BOMBER_WIDTH = (202 * 0.15);
    private static final double BOMBER_HEIGHT = (166 * 0.15);

    public static Wave wave1() {
        return new Wave() {
            @Override
            protected void onStart() {
                for (int i = 0; i < entities.size(); i++) {
                    var e = entities.get(i);

                    var anim = animationBuilder()
                            .repeatInfinitely()
                            .autoReverse(true)
                            .duration(Duration.seconds(10))
                            .delay(Duration.seconds(i * 0.1))
                            .translate(e)
                            .from(new Point2D(0, 0))
                            .to(new Point2D(getAppWidth() - BOMBER_WIDTH, getAppHeight() - BOMBER_HEIGHT))
                            .build();

                    var anim2 = animationBuilder()
                            .duration(Duration.seconds(1))
                            .delay(Duration.seconds(i * 0.1))
                            .fadeIn(e)
                            .build();

                    animations.add(anim);
                    animations.add(anim2);
                }
            }
        };
    }

    public static Wave wave2() {
        return new Wave() {
            @Override
            protected void onStart() {
                for (int i = 0; i < entities.size(); i++) {
                    var e = entities.get(i);

                    var anim = animationBuilder()
                            .repeatInfinitely()
                            .autoReverse(true)
                            .duration(Duration.seconds(10))
                            .delay(Duration.seconds(i * 0.1))
                            .translate(e)
                            .alongPath(new CubicCurve(
                                    0, getAppHeight() - BOMBER_HEIGHT,
                                    getAppWidth() / 3.0, getAppHeight() / 8.0,
                                    getAppWidth() / 1.5, getAppHeight(),
                                    getAppWidth() - BOMBER_HEIGHT, 0
                            ))
                            .build();

                    var anim2 = animationBuilder()
                            .duration(Duration.seconds(1))
                            .delay(Duration.seconds(i * 0.1))
                            .fadeIn(e)
                            .build();

                    animations.add(anim);
                    animations.add(anim2);
                }
            }
        };
    }

    public static Wave wave3() {
        return new Wave() {
            @Override
            protected void onStart() {
                for (int i = 0; i < entities.size(); i++) {
                    var e = entities.get(i);

                    var anim = animationBuilder()
                            .repeatInfinitely()
                            .autoReverse(true)
                            .duration(Duration.seconds(10))
                            .delay(Duration.seconds(i * 0.1))
                            .translate(e)
                            .from(new Point2D(0, getAppHeight() / 2.0 - BOMBER_HEIGHT / 2))
                            .to(new Point2D(getAppWidth() - BOMBER_WIDTH, getAppHeight() / 2.0 - BOMBER_HEIGHT / 2))
                            .build();

                    var anim2 = animationBuilder()
                            .duration(Duration.seconds(1))
                            .delay(Duration.seconds(i * 0.1))
                            .fadeIn(e)
                            .build();

                    animations.add(anim);
                    animations.add(anim2);
                }
            }
        };
    }

    public static Wave wave4() {
        return new Wave() {
            @Override
            protected void onStart() {
                for (int i = 0; i < entities.size(); i++) {
                    var e = entities.get(i);

                    var anim = animationBuilder()
                            .repeatInfinitely()
                            .autoReverse(true)
                            .duration(Duration.seconds(10))
                            .delay(Duration.seconds(i * 0.1))
                            .animate(new AnimatedValue<>(0.0, 10.0))
                            .onProgress(t -> {
                                var x = t / 10.0 * (getAppWidth() - BOMBER_WIDTH);
                                var y = getAppHeight() / 2.0 + FXGLMath.sin(t) * 100;

                                e.setPosition(x, y);
                            })
                            .build();

                    var anim2 = animationBuilder()
                            .duration(Duration.seconds(1))
                            .delay(Duration.seconds(i * 0.1))
                            .fadeIn(e)
                            .build();

                    animations.add(anim);
                    animations.add(anim2);
                }
            }
        };
    }
}
