package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HomingMissileProjectileComponent extends Component {

    private Entity player;

    private boolean isHoming = false;
    private boolean isTracking = false;

    private ProjectileComponent projectileComponent;

    private ParticleComponent particleComponent;
    private ParticleEmitter emitter;

    @Override
    public void onAdded() {
        emitter = particleComponent.getEmitter();

        player = FXGL.geto("player");
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isHoming) {
            projectileComponent.setSpeed(projectileComponent.getSpeed() * 0.98);

            if (projectileComponent.getSpeed() < 15) {
                isHoming = true;
                isTracking = true;

                animationBuilder()
                        .interpolator(Interpolators.BACK.EASE_IN_OUT())
                        .onFinished(() -> {
                            isTracking = false;
                        })
                        .rotate(entity)
                        .from(entity.getRotation())
                        .to(player.getPosition().subtract(entity.getPosition()).angle(1, 0))
                        .buildAndPlay();
            }

            return;
        }

        if (isTracking)
            return;

        Point2D vector = player.getPosition().subtract(entity.getPosition());
        projectileComponent.setDirection(vector);
        projectileComponent.setSpeed(projectileComponent.getSpeed() * 1.05);

        emitter.setSpawnPointFunction((i) -> new Point2D(vector.getY(), -vector.getX()).normalize().multiply(3.5));
    }
}
