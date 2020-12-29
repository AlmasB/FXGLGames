package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.random;
import static com.almasb.fxgl.dsl.FXGL.texture;

public class ExplosionParticleComponent extends ParticleComponent {

    private double t = 0.0;

    public ExplosionParticleComponent() {
        super(ParticleEmitters.newExplosionEmitter(20));
    }

    @Override
    public void onAdded() {
        spawnParticles(entity);
    }

    @Override
    public void onUpdate(double tpf) {
        super.onUpdate(tpf);

        t += tpf;
    }

    private void spawnParticles(Entity enemy) {
        var emitter = getEmitter();

        emitter.setMaxEmissions(1);
        emitter.setNumParticles(200);
        emitter.setEmissionRate(0.86);
        emitter.setSize(1, 14);
        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 1.5)));
        emitter.setSpawnPointFunction(i -> new Point2D(25, 25));
        emitter.setAccelerationFunction(() -> new Point2D(0, 0));

        var c = FXGLMath.randomColor().brighter().brighter();

        //var c = Color.YELLOW;
        var name = "circle_05.png";

        emitter.setSourceImage(texture("particles/" + name, 32, 32).multiplyColor(c));

        emitter.setAllowParticleRotation(true);

        emitter.setControl(p -> {
            var x = p.position.x;
            var y = p.position.y;

            var noiseValue = FXGLMath.noise2D(x * 0.02 * t, y * 0.02 * t);
            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360.0;

            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 15));

            var vx = p.velocity.x * 0.8f + v.x * 0.2f;
            var vy = p.velocity.y * 0.8f + v.y * 0.2f;

            p.velocity.x = vx;
            p.velocity.y = vy;
        });
    }
}
