package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.GameWorld;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxglgames.spaceinvaders.ExplosionEmitter;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossControl extends EnemyControl {

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (attackTimer.elapsed(nextAttack)) {
            shoot();

            nextAttack = Duration.seconds(1);
            attackTimer.capture();
        }
    }

    @Override
    protected void shoot() {
        for (int i = 0; i < 6; i++) {
            GameWorld world = getEntity().getWorld();
            GameEntity bullet = (GameEntity) world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

            bullet.translateX(20 * (i-3));
        }

        FXGL.getAudioPlayer().playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    @Override
    public void die() {
        GameEntity enemy = (GameEntity) getEntity();
        enemy.getComponent(CollidableComponent.class).setValue(false);
        enemy.setControlsEnabled(false);

        for (int i = 0; i < 5; i++) {
            FXGL.getMasterTimer().runOnceAfter(() -> {
                Entity entity = new Entity();
                entity.addComponent(new PositionComponent(enemy.getCenter()));
                entity.addControl(new ParticleControl(new ExplosionEmitter()));
                entity.addControl(new ExpireCleanControl(Duration.seconds(1)));

                enemy.getWorld().addEntity(entity);
                enemy.getWorld().spawn("Explosion", enemy.getCenter().add(FXGLMath.randomPoint2D().multiply(70)));

                FXGL.getAudioPlayer().playSound("explosion.wav");
            }, Duration.seconds(0.25 * i));
        }

        FXGL.getMasterTimer().runOnceAfter(() -> {
            enemy.removeFromWorld();
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_KILLED));
        }, Duration.seconds(1.8));
    }
}
