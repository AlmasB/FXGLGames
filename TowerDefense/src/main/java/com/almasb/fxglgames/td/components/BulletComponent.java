package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.td.EntityType;
import com.almasb.fxglgames.td.TowerDefenseApp;
import com.almasb.fxglgames.td.data.Config;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletComponent extends Component {

    private Entity tower;
    private Entity target;

    public BulletComponent(Entity tower, Entity target) {
        this.tower = tower;
        this.target = target;
    }

    @Override
    public void onUpdate(double tpf) {
        if (!target.isActive()) {
            entity.removeFromWorld();
            return;
        }

        if (entity.distanceBBox(target) < Config.BULLET_SPEED * tpf) {
            onTargetHit();
            return;
        }

        entity.translateTowards(target.getCenter(), Config.BULLET_SPEED * tpf);
    }

    private void onTargetHit() {
        TowerComponent tower = this.tower.getComponent(TowerComponent.class);
        var data = tower.getData();

        var targets = new ArrayList<Entity>();

        if (data.isSplashDamage()) {
            // TODO: extract range? perhaps can use aoeRange = 0 for no splash damage, any greater value for range itself

            targets.addAll(
                    getGameWorld().getEntitiesInRange(target.getBoundingBoxComponent().range(5, 5))
                            .stream()
                            .filter(e -> e.isType(EntityType.ENEMY))
                            .toList()
            );

        } else {
            targets.add(target);
        }

        tower.onHitEffects().forEach(effect -> {
            if (FXGLMath.randomBoolean(effect.getChance())) {
                targets.forEach(t -> {
                    effect.onTriggered(tower, t);

                    t.getComponent(EffectComponent.class).startEffect(effect.getEffect());
                });

                // TODO: generalise
                //var visualEffect = spawn("visualEffectSlow", target.getPosition());

                //Animations.playVisualEffectSlowAnimation(visualEffect);
            }
        });

        entity.removeFromWorld();

        targets.forEach(t -> {
            var hp = t.getComponent(HealthIntComponent.class);

            var damage = (int) (tower.getDamage() * tower.getDamageModifier());

            hp.damage(damage);

            tower.resetDamageModifier();

            if (hp.isZero()) {
                FXGL.<TowerDefenseApp>getAppCast().killEnemy(t);
            }
        });
    }
}
