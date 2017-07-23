package com.almasb.fxglgames.td.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.td.TowerDefenseType;
import com.almasb.fxglgames.td.Config;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerControl extends Control {

    private LocalTimer shootTimer;

    @Override
    public void onAdded(Entity entity) {
        shootTimer = FXGL.newLocalTimer();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (shootTimer.elapsed(Duration.seconds(0.5))) {
            FXGL.getApp()
                    .getGameWorld()
                    .getClosestEntity(entity, e -> Entities.getType(e).isType(TowerDefenseType.ENEMY))
                    .ifPresent(nearestEnemy -> {
                        shoot(nearestEnemy);
                        shootTimer.capture();
                    });
        }
    }

    private void shoot(Entity enemy) {
        Point2D position = Entities.getPosition(getEntity()).getValue();

        Point2D direction = Entities.getPosition(enemy)
                .getValue()
                .subtract(position);

        Entity bullet = FXGL.getApp().getGameWorld().spawn("Bullet", position);
        bullet.addControl(new ProjectileControl(direction, Config.BULLET_SPEED));
    }
}
