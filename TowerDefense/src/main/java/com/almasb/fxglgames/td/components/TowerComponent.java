package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.Config;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerComponent extends Component {

    private LocalTimer shootTimer;

    private int hp = 10;
    private int damage = 1;
    private double attackDelay = 1.5;

    public int getHP() {
        return hp;
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackDelay() {
        return attackDelay;
    }

    @Override
    public void onAdded() {
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {

        if (shootTimer.elapsed(Duration.seconds(0.5))) {
            FXGL.getGameWorld()
                    .getClosestEntity(entity, e -> e.isType(EntityType.ENEMY))
                    .ifPresent(nearestEnemy -> {
                        shoot(nearestEnemy);
                        shootTimer.capture();
                    });
        }
    }

    private void shoot(Entity enemy) {
        Point2D position = getEntity().getPosition();

        Point2D direction = enemy.getPosition().subtract(position);

        Entity bullet = FXGL.spawn("Bullet", position);
        bullet.addComponent(new ProjectileComponent(direction, Config.BULLET_SPEED));
    }
}
