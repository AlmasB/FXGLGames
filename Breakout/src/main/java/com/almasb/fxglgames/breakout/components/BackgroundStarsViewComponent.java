package com.almasb.fxglgames.breakout.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.run;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BackgroundStarsViewComponent extends ChildViewComponent {

    private Texture[] stars;

    public BackgroundStarsViewComponent(Texture... stars) {
        this.stars = stars;

        for (var t : stars) {
            getViewRoot().getChildren().add(t);
        }

        run(() -> onUpdate(0.016), Duration.seconds(0.016));
    }

    @Override
    public void onUpdate(double tpf) {
        for (var t : stars) {
            move(t, tpf);
        }
    }

    private void move(Texture star, double tpf) {
        star.setTranslateY(star.getTranslateY() - tpf * FXGLMath.random(1, 30));

        if (star.getTranslateY() + star.getHeight() <= getAppHeight()) {
            star.setTranslateY(0);
        }
    }
}
