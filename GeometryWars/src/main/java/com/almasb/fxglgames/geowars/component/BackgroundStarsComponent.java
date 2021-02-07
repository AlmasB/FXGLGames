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

    private Texture starsLayerFront1;
    private Texture starsLayerBack1;
    private Texture starsLayerBack2;

    @Override
    public void onAdded() {
        var size = getAppWidth() + OUTSIDE_DISTANCE*2;

        starsLayerFront1 = texture("background/stars_big_1.png", size, size);

        starsLayerBack1 = texture("background/stars_small_1.png", size, size);
        starsLayerBack2 = texture("background/stars_small_2.png", size, size);

        entity.getViewComponent().addChild(starsLayerFront1);
        entity.getViewComponent().addChild(starsLayerBack1);
        entity.getViewComponent().addChild(starsLayerBack2);
    }

    @Override
    public void onUpdate(double tpf) {
        var viewport = getGameScene().getViewport();

        starsLayerFront1.setTranslateX(-OUTSIDE_DISTANCE -FRONT1_SPEED * viewport.getX());
        starsLayerFront1.setTranslateY(-OUTSIDE_DISTANCE -FRONT1_SPEED * viewport.getY());

        starsLayerBack1.setTranslateX(-OUTSIDE_DISTANCE -BACK1_SPEED * viewport.getX());
        starsLayerBack1.setTranslateY(-OUTSIDE_DISTANCE -BACK1_SPEED * viewport.getY());

        starsLayerBack2.setTranslateX(-OUTSIDE_DISTANCE -BACK2_SPEED * viewport.getX());
        starsLayerBack2.setTranslateY(-OUTSIDE_DISTANCE -BACK2_SPEED * viewport.getY());
    }
}
