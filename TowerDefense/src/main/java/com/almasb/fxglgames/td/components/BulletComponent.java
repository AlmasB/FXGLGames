package com.almasb.fxglgames.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.td.Config;
import com.almasb.fxglgames.td.event.EnemyKilledEvent;

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

        entity.removeFromWorld();

        var hp = target.getComponent(HealthIntComponent.class);

        hp.damage(data.getDamage());

        if (hp.isZero()) {
            FXGL.getEventBus().fireEvent(new EnemyKilledEvent(target));
            target.removeFromWorld();
        }
    }
}
