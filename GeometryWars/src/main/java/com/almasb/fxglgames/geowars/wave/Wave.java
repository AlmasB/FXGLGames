package com.almasb.fxglgames.geowars.wave;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.entity.Entity;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Represents a single wave of enemies.
 * Currently hardcoded to use Bomber type enemy, but easy to generalize if needed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Wave {

    protected final Array<Entity> entities = new Array<>(100);

    protected final Array<Animation<?>> animations = new Array<>(100);

    public final void start() {
        for (int i = 0; i < 100; i++) {
            var e = spawn("Bomber");
            e.setOpacity(0);

            entities.add(e);
        }

        onStart();

        animations.forEach(Animation::start);
    }

    public final void end() {
        animations.forEach(Animation::stop);
        animations.clear();

        entities.forEach(e -> {
            if (e.isActive()) {
                e.removeFromWorld();
            }
        });
        entities.clear();

        onEnd();
    }

    protected void onStart() {

    }

    public void onUpdate(double tpf) {
        animations.forEach(anim -> anim.onUpdate(tpf));
    }

    protected void onEnd() {

    }
}
