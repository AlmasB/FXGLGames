package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.play;
import static com.almasb.fxgl.app.DSLKt.spawn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossComponent extends EnemyControl {

    @Override
    public void onUpdate(double tpf) {
//        if (attackTimer.elapsed(nextAttack)) {
//            shoot();
//
//            nextAttack = Duration.seconds(1);
//            attackTimer.capture();
//        }
    }

    @Override
    protected void shoot() {
//        for (int i = 0; i < 6; i++) {
//            GameWorld world = getEntity().getWorld();
//            Entity bullet = world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));
//
//            bullet.translateX(20 * (i-3));
//        }
//
//        play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    @Override
    public void die() {
        Entity enemy = getEntity();
        enemy.getComponent(CollidableComponent.class).setValue(false);
        enemy.setUpdateEnabled(false);

        for (int i = 0; i < 5; i++) {
            FXGL.getMasterTimer().runOnceAfter(() -> {
                spawn("Explosion", enemy.getCenter().add(FXGLMath.randomPoint2D().multiply(70)));
            }, Duration.seconds(0.25 * i));
        }

        FXGL.getMasterTimer().runOnceAfter(() -> {
            enemy.removeFromWorld();
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_KILLED));
        }, Duration.seconds(1.8));
    }
}
