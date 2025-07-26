package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.buffs.CritEffect;
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

    private List<OnHitEffect> effects;

    private double damageModifier = 1.0;

    private TowerData data;

    public TowerComponent(TowerData data) {
        this.data = data;

        effects = data.effects()
                .stream()
                .map(e -> parseEffect(e))
                .toList();
    }

    public double getDamageModifier() {
        return damageModifier;
    }

    public void setDamageModifier(double damageModifier) {
        this.damageModifier = damageModifier;
    }

    public void resetDamageModifier() {
        damageModifier = 1.0;
    }

    public int getDamage() {
        return data.attack();
    }

    public List<OnHitEffect> onHitEffects() {
        return effects;
    }

    @Override
    public void onAdded() {
        shootTimer = newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        var interval = Duration.seconds(1.0 / data.attackRate());

        if (shootTimer.elapsed(interval)) {
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

    private OnHitEffect parseEffect(String effect) {
        var tokens = effect.split(",");

        if (effect.startsWith("crit")) {
            double chance = Double.parseDouble(tokens[1]) * 0.01;
            double modifier = Double.parseDouble(tokens[2]);

            return new CritEffect(chance, modifier);
        }

        if (effect.startsWith("slow")) {

        }

        throw new IllegalArgumentException("Unknown effect: " + effect);
    }
}
