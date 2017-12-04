package com.almasb.fxglgames.bomberman.control;

import com.almasb.fxglgames.bomberman.BombermanApp;
import com.almasb.fxglgames.bomberman.BombermanType;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombControl extends Control {

    private int radius;

    public BombControl(int radius) {
        this.radius = radius;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) { }

    public void explode() {
        BoundingBoxComponent bbox = getEntity().getBoundingBoxComponent();

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bbox.range(radius, radius))
                .stream()
                .filter(e -> e.isType(BombermanType.BRICK))
                .forEach(e -> {
                    FXGL.<BombermanApp>getAppCast().onWallDestroyed(e);
                    e.removeFromWorld();
                });

        getEntity().removeFromWorld();
    }
}
