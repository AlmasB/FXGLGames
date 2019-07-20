package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CatapultComponent extends Component {

    private AnimatedTexture texture;

    @Override
    public void onAdded() {
        entity.setScaleX(-1);
        texture = FXGL.texture("catapult2.png", 2450 / 2.5, 350 / 2.5).toAnimatedTexture(7, Duration.seconds(1));
        entity.getViewComponent().clearChildren();
        entity.getViewComponent().addChild(texture);

        texture.setOnCycleFinished(() -> texture.stop());
    }

    @Override
    public void onUpdate(double tpf) {
        texture.onUpdate(tpf);
    }

    public void activate() {
        texture.play();
    }
}
