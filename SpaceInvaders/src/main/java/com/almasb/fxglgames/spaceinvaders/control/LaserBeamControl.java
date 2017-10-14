package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;

import static com.almasb.fxgl.app.DSLKt.getd;
import static com.almasb.fxgl.app.DSLKt.inc;
import static com.almasb.fxgl.app.DSLKt.set;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LaserBeamControl extends Control {

    @Override
    public void onUpdate(Entity entity, double tpf) {
        inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);

        if (getd("laserMeter") <= 0) {
            set("laserMeter", 0.0);
            entity.removeFromWorld();
        }
    }
}
