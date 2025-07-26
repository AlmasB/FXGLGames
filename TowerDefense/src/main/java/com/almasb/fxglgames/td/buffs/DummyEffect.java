package com.almasb.fxglgames.td.buffs;

import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public final class DummyEffect extends Effect {
    
    public DummyEffect() {
        super(Duration.seconds(0.01));
    }

    @Override
    public void onStart(Entity entity) { }

    @Override
    public void onEnd(Entity entity) { }
}
