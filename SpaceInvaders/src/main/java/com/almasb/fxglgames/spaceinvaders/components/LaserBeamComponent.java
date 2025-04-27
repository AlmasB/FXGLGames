

package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.spaceinvaders.Config;

import com.almasb.fxgl.dsl.FXGL;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LaserBeamComponent extends Component {

    private FXGLInterface fxgl;

    public LaserBeamComponent() {
        this(new DefaultFXGLWrapper());
    }

    // Constructor for testing
    public LaserBeamComponent(FXGLInterface fxgl) {
        this.fxgl = fxgl;
    }

    @Override
    public void onUpdate(double tpf) {
        fxgl.inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);

        if (fxgl.getd("laserMeter") <= 0) {
            fxgl.set("laserMeter", 0.0);
            entity.removeFromWorld();
        }
    }

    public interface FXGLInterface {
        void inc(String varName, double value);
        double getd(String varName);
        void set(String varName, double value);
    }

    private static class DefaultFXGLWrapper implements FXGLInterface {
        @Override
        public void inc(String varName, double value) {
            FXGL.inc(varName, value);
        }

        @Override
        public double getd(String varName) {
            return FXGL.getd(varName);
        }

        @Override
        public void set(String varName, double value) {
            FXGL.set(varName, value);
        }
    }
}
