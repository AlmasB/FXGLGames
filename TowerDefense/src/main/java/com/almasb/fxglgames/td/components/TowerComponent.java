package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.buffs.OnHitEffect;
import com.almasb.fxglgames.td.data.TowerData;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;

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

    // TODO: read from data
    public List<OnHitEffect> onHitEffects() {
        return List.of(
                new OnHitEffect(new SlowTimeEffect(0.2, Duration.seconds(3)), 0.75)
        );
    }

    @Override
    public void onAdded() {
        shootTimer = newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (shootTimer.elapsed(Duration.seconds(1.5))) {
            getGameWorld()
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

        String imageName = Objects.requireNonNullElse(data.projectileImageName(), "projectile.png");

        var bullet = spawn("Bullet",
                new SpawnData(position)
                        .put("imageName", imageName)
                        .put("tower", entity)
                        .put("target", enemy)
        );
        bullet.rotateToVector(direction);
    }
}
