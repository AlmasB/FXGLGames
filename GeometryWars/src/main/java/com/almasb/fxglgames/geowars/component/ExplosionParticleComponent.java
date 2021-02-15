package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class ExplosionParticleComponent extends Component {

    private Array<Particle> particles = new UnorderedArray<>();

    private GridComponent grid;

    private Color color;

    private int numParticles;

    public ExplosionParticleComponent(int numParticles) {
        this.numParticles = numParticles;
    }

    @Override
    public void onAdded() {
        grid = byType(GeoWarsType.GRID).get(0).getComponent(GridComponent.class);

        color = Color.rgb(random(50, 235), random(155, 255), random(50, 235)).brighter();

        spawnParticles();
    }

    public void onBackgroundUpdate(double tpf, GraphicsContext g) {
        for (var iter = particles.iterator(); iter.hasNext(); ) {
            var p = iter.next();

            if (p.update(g, tpf)) {
                iter.remove();
            }
        }

        if (particles.isEmpty()) {
            entity.removeFromWorld();
        }
    }

    private void spawnParticles() {
        var pos = entity.getPosition();

        for (int i = 0; i < numParticles; i++) {
            var p = new Particle();
            p.radius = random(1, 6);
            p.initialLife = random(0.25, 1.5) + 0.5;
            p.life = p.initialLife;
            p.position.set(pos.add(25, 25));
            p.color = color;

            var angle = FXGLMath.map(i, 0, 200, 0, 360);
            p.velocity.setFromAngle(angle);
            p.acceleration.set(p.velocity);

            particles.add(p);
        }
    }

    private static class Particle {
        private Vec2 position = new Vec2();
        private Vec2 velocity = new Vec2();
        private Vec2 acceleration = new Vec2();
        private double life = 0.0;
        private double initialLife = 0.0;
        private float radius = 0.0f;
        private Color color = Color.BLACK;

        boolean update(GraphicsContext g, double tpf) {
            double progress = 1 - life / initialLife;
            double t = progress;

            var p = this;

            var x = p.position.x;
            var y = p.position.y;

            var noiseValue = FXGLMath.noise2D(x * 0.02 * t, y * 0.02 * t);
            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360.0;

            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 15));

            var vx = (p.velocity.x * 0.8f + v.x * 0.2f) * 1.15f;
            var vy = (p.velocity.y * 0.8f + v.y * 0.2f) * 1.15f;

            p.velocity.x = vx;
            p.velocity.y = vy;

            velocity.addLocal(acceleration);
            x = position.x + velocity.x;
            y = position.y + velocity.y;


            var size = radius;

            if (x + size >= getAppWidth()) {
                p.position.x = getAppWidth() - size;
                p.velocity.x = -FXGLMath.abs(p.velocity.x) * 2;
            }

            if (x <= 0) {
                p.position.x = 0;
                p.velocity.x = FXGLMath.abs(p.velocity.x) * 2;
            }

            if (y + size >= getAppHeight()) {
                p.position.y = getAppHeight() - size;
                p.velocity.y = -FXGLMath.abs(p.velocity.y) * 2;
            }

            if (y <= 0) {
                p.position.y = 0;
                p.velocity.y = FXGLMath.abs(p.velocity.y) * 2;
            }

            position.set((float) x, (float) y);

            //radius.addLocal(scale);

            life -= tpf;

            boolean dead = life <= 0 || radius <= 0;

            if (!dead) {
                double alpha = life / initialLife;

                g.setGlobalAlpha(alpha);
                g.setFill(color);
                g.fillOval(p.position.x, p.position.y, size * 2, size * 2);
            }

            return dead;
        }
    }
}
