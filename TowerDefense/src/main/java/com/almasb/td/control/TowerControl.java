package com.almasb.td.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.td.Config;
import com.almasb.td.EntityFactory;
import com.almasb.td.EntityType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerControl extends AbstractControl {

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
                    .getClosestEntity(entity, e -> Entities.getType(e).isType(EntityType.ENEMY))
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

        GameEntity bullet = EntityFactory.spawnBullet(position.getX(), position.getY());
        bullet.addControl(new ProjectileControl(direction, Config.BULLET_SPEED));

        FXGL.getApp().getGameWorld().addEntity(bullet);
    }
}
