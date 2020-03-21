package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BackgroundStarsViewComponent extends ChildViewComponent {

    private Texture stars;

    public BackgroundStarsViewComponent(Texture stars) {
        this.stars = stars;

        getViewRoot().getChildren().add(stars);

        FXGL.run(() -> onUpdate(0.016), Duration.seconds(0.016));
    }

    @Override
    public void onUpdate(double tpf) {
        stars.setTranslateY(stars.getTranslateY() - tpf * FXGLMath.random(1, 30));

        if (stars.getTranslateY() + stars.getHeight() <= FXGL.getAppHeight()) {
            stars.setTranslateY(0);
        }
    }
}
