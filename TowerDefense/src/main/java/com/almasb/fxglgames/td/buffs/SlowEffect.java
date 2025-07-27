package com.almasb.fxglgames.td.buffs;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import javafx.util.Duration;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class SlowEffect extends OnHitEffect {

    private final double ratio;
    private final double duration;

    public SlowEffect(double ratio, double duration, double chance) {
        super(new DummyEffect(), chance);
        this.ratio = ratio;
        this.duration = duration;
    }

    @Override
    public Effect getEffect() {
        return new SlowTimeEffect(ratio, Duration.seconds(duration));
    }
}
