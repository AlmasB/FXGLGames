package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.spaceinvaders.Config;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LaserBeamComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);

        if (getd("laserMeter") <= 0) {
            set("laserMeter", 0.0);
            entity.removeFromWorld();
        }
    }
}
