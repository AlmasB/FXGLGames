package com.almasb.fxglgames.td.buffs;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.td.components.TowerComponent;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class CritEffect extends OnHitEffect {

    private final double damageModifier;

    public CritEffect(double chance, double damageModifier) {
        super(new DummyEffect(), chance);
        this.damageModifier = damageModifier;
    }

    @Override
    public void onTriggered(TowerComponent tower, Entity target) {
        tower.setDamageModifier(tower.getDamageModifier() * damageModifier);
    }
}
