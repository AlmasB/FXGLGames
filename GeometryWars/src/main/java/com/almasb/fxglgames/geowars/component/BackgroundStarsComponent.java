package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.Config.OUTSIDE_DISTANCE;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BackgroundStarsComponent extends Component {

    private static final double FRONT1_SPEED = 0.25;
    private static final double BACK1_SPEED = 0.07;
    private static final double BACK2_SPEED = 0.02;

    private Texture starsLayer0;
    private Texture starsLayer1;
    private Texture starsLayer2;

    @Override
    public void onAdded() {
        starsLayer0 = texture("background/bg_0.png");
        starsLayer1 = texture("background/bg_1.png");
        starsLayer2 = texture("background/bg_2.png");

        entity.getViewComponent().addChild(starsLayer0);
        entity.getViewComponent().addChild(starsLayer1);
        entity.getViewComponent().addChild(starsLayer2);

        entity.getViewComponent().getChildren().forEach(v -> v.setOpacity(0.5));

        entity.setOpacity(0.75);
    }

    @Override
    public void onUpdate(double tpf) {
        var viewport = getGameScene().getViewport();

        starsLayer0.setTranslateX(-OUTSIDE_DISTANCE -FRONT1_SPEED * viewport.getX());
        starsLayer0.setTranslateY(-OUTSIDE_DISTANCE -FRONT1_SPEED * viewport.getY());

        starsLayer1.setTranslateX(-OUTSIDE_DISTANCE -BACK1_SPEED * viewport.getX());
        starsLayer1.setTranslateY(-OUTSIDE_DISTANCE -BACK1_SPEED * viewport.getY());

        starsLayer2.setTranslateX(-OUTSIDE_DISTANCE -BACK2_SPEED * viewport.getX());
        starsLayer2.setTranslateY(-OUTSIDE_DISTANCE -BACK2_SPEED * viewport.getY());
    }
}
