package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BouncerComponent extends Component {

    private int moveSpeed;

    private Animation<?> animation;

    public BouncerComponent(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded() {
        double seconds = (FXGL.getAppWidth() - entity.getWidth()) / moveSpeed;

        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(seconds))
                .repeatInfinitely()
                .autoReverse(true)
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .translate(entity)
                .from(entity.getPosition())
                .to(new Point2D(FXGL.getAppWidth() - entity.getWidth(), entity.getY()))
                .build();

        animation.start();
    }

    @Override
    public void onUpdate(double tpf) {
        animation.onUpdate(tpf);

        entity.setScaleX(animation.isReverse() ? -1 : 1);
    }

    @Override
    public void onRemoved() {
        animation.stop();
    }
}
