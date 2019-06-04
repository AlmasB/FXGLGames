package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyZombieComponent extends Component {

    private LiftComponent lift;

    private AnimatedTexture texture;

    private AnimationChannel animWalk;

    public EnemyZombieComponent() {
        int w = 1392 / 4;
        int h = 390 / 4;

        animWalk = new AnimationChannel(FXGL.image("enemies/zombie/zombie1.png", w, h), 6, 232 / 4, h, Duration.seconds(0.75), 0, 5);

        texture = new AnimatedTexture(animWalk);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(232 / 4 / 2, 390 / 4 / 2));
        entity.setView(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        entity.setScaleX(lift.isGoingRight() ? 1 : -1);
    }
}
