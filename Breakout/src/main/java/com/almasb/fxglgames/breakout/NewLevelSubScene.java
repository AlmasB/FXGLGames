package com.almasb.fxglgames.breakout;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.particle.ParticleSystem;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NewLevelSubScene extends SubScene {

    private ParticleSystem particleSystem;
    
    public NewLevelSubScene(int level) {
        var emitter = ParticleEmitters.newExplosionEmitter(50);

        var t = texture("particles/trace_horizontal.png", 128.0, 128.0);

        emitter.setSourceImage(t);
        emitter.setSize(116, 128);
        emitter.setEmissionRate(0.12);
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(20);
        emitter.setVelocityFunction(i -> new Point2D(random(-100, 100), 0));
        emitter.setExpireFunction(i -> Duration.seconds(random(1, 3)));
        emitter.setBlendMode(getSettings().isNative() ? BlendMode.SRC_OVER : BlendMode.ADD);

        particleSystem = new ParticleSystem();
        particleSystem.addParticleEmitter(emitter, getAppWidth() / 2.0 - 120, getAppHeight() / 2.0 - 110);

        var text = getUIFactoryService().newText("LEVEL " + level, Color.GOLD, 48);
        text.setTranslateX(getAppWidth() / 2.0 + 20 - 100);
        text.setTranslateY(getAppHeight() / 2.0 + 130 - 100);

        if (!getSettings().isNative()) {
            getContentRoot().getChildren().addAll(particleSystem.getPane());
        }
        getContentRoot().getChildren().addAll(text);

        animationBuilder()
                .onFinished(() -> {
                    animationBuilder()
                            .onFinished(() -> getSceneService().popSubScene())
                            .delay(Duration.seconds(2.0))
                            .duration(Duration.seconds(0.5))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                            .translate(getContentRoot())
                            .from(new Point2D(0, 0))
                            .to(new Point2D(1050, 0))
                            .buildAndPlay(this);
                })
                .delay(Duration.seconds(0.3))
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(getContentRoot())
                .from(new Point2D(-1050, 0))
                .to(new Point2D(0, 0))
                .buildAndPlay(this);
    }

    @Override
    protected void onUpdate(double tpf) {
        particleSystem.onUpdate(tpf);
    }
}
