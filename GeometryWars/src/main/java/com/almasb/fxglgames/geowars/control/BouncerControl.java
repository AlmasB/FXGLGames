package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BouncerControl extends Control {

    private int moveSpeed;

    private Animation<?> animation;

    public BouncerControl(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded(Entity entity) {
        double seconds = (FXGL.getAppWidth() - entity.getWidth()) / moveSpeed;

        animation = Entities.animationBuilder()
                .duration(Duration.seconds(seconds))
                .repeat(Integer.MAX_VALUE)
                .autoReverse(true)
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .translate(entity)
                .from(entity.getPosition())
                .to(new Point2D(FXGL.getAppWidth() - entity.getWidth(), entity.getY()))
                .buildAndPlay();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) { }

    @Override
    public void onRemoved(Entity entity) {
        animation.stop();
    }
}
