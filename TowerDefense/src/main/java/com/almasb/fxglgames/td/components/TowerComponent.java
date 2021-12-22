package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.Config;
import com.almasb.fxglgames.td.TowerData;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 *
 * // TODO: assign bullet data from tower that shot it
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerComponent extends Component {

    private LocalTimer shootTimer;

    private TowerData data;

    public TowerComponent(TowerData data) {
        this.data = data;
    }

    public int getDamage() {
        return data.attack();
    }

    @Override
    public void onAdded() {
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (shootTimer.elapsed(Duration.seconds(1.5))) {
            FXGL.getGameWorld()
                    .getClosestEntity(entity, e -> e.isType(EntityType.ENEMY))
                    .ifPresent(nearestEnemy -> {

                        entity.rotateToVector(nearestEnemy.getPosition().subtract(entity.getPosition()));
                        // because of sprite rotation
                        entity.rotateBy(90);

                        shoot(nearestEnemy);
                        shootTimer.capture();
                    });
        }
    }

    private void shoot(Entity enemy) {
        Point2D position = getEntity().getPosition();

        Point2D direction = enemy.getPosition().subtract(position);

        Entity bullet = FXGL.spawn("Bullet", position.add(32, 32));
        bullet.setProperty("tower", entity);
        bullet.addComponent(new ProjectileComponent(direction, Config.BULLET_SPEED));
    }
}
