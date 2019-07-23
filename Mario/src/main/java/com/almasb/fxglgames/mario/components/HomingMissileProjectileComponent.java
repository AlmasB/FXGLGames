package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HomingMissileProjectileComponent extends Component {

    private TimerAction action;
    private Entity player;

    private TimeComponent time;
    private boolean isHoming = false;

    private ParticleEmitter emitter;

    @Override
    public void onAdded() {
        emitter = ParticleEmitters.newSmokeEmitter();
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(1, 4);
        emitter.setSpawnPointFunction((i) -> new Point2D(7, 13));
        emitter.setStartColor(Color.LIGHTGRAY);
        emitter.setEndColor(Color.BLACK);

        action = runOnce(() -> {
            time.setValue(0.3);

            entity.addComponent(new ParticleComponent(emitter));

            action = runOnce(() -> {
                time.setValue(1.0);
                isHoming = true;

            }, Duration.seconds(2));

        }, Duration.seconds(2));
    }

    @Override
    public void onUpdate(double tpf) {
        if (!isHoming)
            return;

        if (player == null) {
            player = FXGL.geto("player");
        }

        Point2D vector = player.getPosition().subtract(entity.getPosition());
        entity.getComponent(ProjectileComponent.class).setDirection(vector);
        entity.getComponent(ProjectileComponent.class).setSpeed(450);

        emitter.setSpawnPointFunction((i) -> new Point2D(vector.getY(), -vector.getX()).normalize().multiply(3.5));
    }

    @Override
    public void onRemoved() {
        if (action != null)
            action.expire();
    }
}
