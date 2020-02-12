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

    private String[] names = ("circle_01.png\n" +
            "circle_02.png\n" +
            "circle_03.png\n" +
            "circle_04.png\n" +
            "circle_05.png\n" +
            "dirt_01.png\n" +
            "dirt_02.png\n" +
            "dirt_03.png\n" +
            "fire_01.png\n" +
            "fire_02.png\n" +
            "flame_01.png\n" +
            "flame_02.png\n" +
            "flame_03.png\n" +
            "flame_04.png\n" +
            "flame_05.png\n" +
            "flame_06.png\n" +
            "flare_01.png\n" +
            "light_01.png\n" +
            "light_02.png\n" +
            "light_03.png\n" +
            "magic_01.png\n" +
            "magic_02.png\n" +
            "magic_03.png\n" +
            "magic_04.png\n" +
            "magic_05.png\n" +
            "muzzle_01.png\n" +
            "muzzle_02.png\n" +
            "muzzle_03.png\n" +
            "muzzle_04.png\n" +
            "muzzle_05.png\n" +
            "scorch_01.png\n" +
            "scorch_02.png\n" +
            "scorch_03.png\n" +
            "scratch_01.png\n" +
            "slash_01.png\n" +
            "slash_02.png\n" +
            "slash_03.png\n" +
            "slash_04.png\n" +
            "smoke_01.png\n" +
            "smoke_02.png\n" +
            "smoke_03.png\n" +
            "smoke_04.png\n" +
            "smoke_05.png\n" +
            "smoke_06.png\n" +
            "smoke_07.png\n" +
            "smoke_08.png\n" +
            "smoke_09.png\n" +
            "smoke_10.png\n" +
            "spark_01.png\n" +
            "spark_02.png\n" +
            "spark_03.png\n" +
            "spark_04.png\n" +
            "spark_05.png\n" +
            "spark_06.png\n" +
            "spark_07.png\n" +
            "star_01.png\n" +
            "star_02.png\n" +
            "star_03.png\n" +
            "star_04.png\n" +
            "star_05.png\n" +
            "star_06.png\n" +
            "star_07.png\n" +
            "star_08.png\n" +
            "star_09.png\n" +
            "symbol_01.png\n" +
            "symbol_02.png\n" +
            "trace_01.png\n" +
            "trace_02.png\n" +
            "trace_03.png\n" +
            "trace_04.png\n" +
            "trace_05.png\n" +
            "trace_06.png\n" +
            "trace_07.png\n" +
            "twirl_01.png\n" +
            "twirl_02.png\n" +
            "twirl_03.png\n" +
            "window_01.png\n" +
            "window_02.png\n" +
            "window_03.png\n" +
            "window_04.png").split("\n");

    private static int index = 0;

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

//        if (index == names.length) {
//            index = 0;
//        }

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
