package com.almasb.bomberman.control;

import com.almasb.bomberman.BombermanApp;
import com.almasb.bomberman.BombermanType;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombControl extends AbstractControl {

    private int radius;

    public BombControl(int radius) {
        this.radius = radius;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void explode() {
        BoundingBoxComponent bbox = Entities.getBBox(getEntity());

        FXGL.getApp()
                .getGameWorld()
                .getEntitiesInRange(bbox.range(radius, radius))
                .stream()
                .filter(e -> Entities.getType(e).isType(BombermanType.BRICK))
                .forEach(e -> {
                    FXGL.<BombermanApp>getAppCast().onWallDestroyed(e);
                    e.removeFromWorld();
                });

        getEntity().removeFromWorld();
    }
}
