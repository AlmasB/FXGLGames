package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.td.TowerDefenseApp;
import com.almasb.fxglgames.td.data.Config;
import com.almasb.fxglgames.td.ui.Animations;

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
        TowerComponent data = tower.getComponent(TowerComponent.class);

        data.onHitEffects().forEach(effect -> {
            if (FXGLMath.randomBoolean(effect.getChance())) {
                target.getComponent(EffectComponent.class).startEffect(effect.getEffect());

                // TODO: generalise
                var visualEffect = spawn("visualEffectSlow", target.getPosition());

                Animations.playVisualEffectSlowAnimation(visualEffect);
            }
        });

        entity.removeFromWorld();

        var hp = target.getComponent(HealthIntComponent.class);

        hp.damage(data.getDamage());

        if (hp.isZero()) {
            FXGL.<TowerDefenseApp>getAppCast().onEnemyKilled(target);
            target.removeFromWorld();
        }
    }
}
