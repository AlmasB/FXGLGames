package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.geowars.GeoWarsType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalControl extends Control {

    @Override
    public void onUpdate(Entity entity, double tpf) {
        rotate(tpf);
        followPlayer(tpf);
    }

    private void rotate(double tpf) {
        getEntity().rotateBy(180 * tpf);

        if (getEntity().getRotation() >= 360) {
            getEntity().setRotation(0);
        }
    }

    private void followPlayer(double tpf) {
        Entity player = getEntity().getWorld().getSingleton(GeoWarsType.PLAYER).get();
        if (getEntity().distance(player) < 100) {
            getEntity().translateTowards(player.getCenter(), 100 * tpf);
        }
    }
}
