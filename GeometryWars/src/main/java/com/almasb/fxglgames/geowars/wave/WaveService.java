package com.almasb.fxglgames.geowars.wave;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.math.FXGLMath;

import static com.almasb.fxglgames.geowars.wave.WaveFactory.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WaveService extends EngineService {

    private static final Wave[] waves = new Wave[] {
            wave1(),
            wave2(),
            wave3(),
            wave4()
    };

    private Wave wave = null;

    public void spawnWave() {
        if (wave != null) {
            wave.end();
        }

        wave = FXGLMath.random(waves).get();
        wave.start();
    }

    @Override
    public void onGameUpdate(double tpf) {
        if (wave != null) {
            wave.onUpdate(tpf);
        }
    }
}
