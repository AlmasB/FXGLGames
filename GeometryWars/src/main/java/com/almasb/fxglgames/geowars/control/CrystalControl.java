package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxglgames.geowars.GeoWarsType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalControl extends Control {

    private GameEntity crystal;

    @Override
    public void onAdded(Entity entity) {
        crystal = (GameEntity) entity;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        rotate(tpf);
        followPlayer(tpf);
    }

    private void rotate(double tpf) {
        crystal.rotateBy(180 * tpf);

        if (crystal.getRotation() >= 360) {
            crystal.setRotation(0);
        }
    }

    private void followPlayer(double tpf) {
        GameEntity player = (GameEntity) getEntity().getWorld().getSingleton(GeoWarsType.PLAYER).get();
        if (crystal.distance(player) < 100) {
            crystal.translateTowards(player.getCenter(), 100 * tpf);
        }
    }
}
