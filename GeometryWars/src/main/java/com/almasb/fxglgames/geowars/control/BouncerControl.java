package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BouncerControl extends Control {

    private GameEntity bouncer;

    private int moveSpeed;

    private Animation<?> animation;

    public BouncerControl(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded(Entity entity) {
        bouncer = (GameEntity) entity;

        double seconds = (FXGL.getAppWidth() - bouncer.getWidth()) / moveSpeed;

        animation = Entities.animationBuilder()
                .duration(Duration.seconds(seconds))
                .repeat(Integer.MAX_VALUE)
                .autoReverse(true)
                .interpolator(Interpolators.EXPONENTIAL.EASE_IN_OUT())
                .translate(bouncer)
                .from(bouncer.getPosition())
                .to(new Point2D(FXGL.getAppWidth() - bouncer.getWidth(), bouncer.getY()))
                .buildAndPlay();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) { }

    @Override
    public void onRemoved(Entity entity) {
        animation.stop();
    }
}
