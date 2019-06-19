package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerCrystalHandler extends CollisionHandler {

    public PlayerCrystalHandler() {
        super(GeoWarsType.PLAYER, GeoWarsType.CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity crystal) {

        crystal.getComponent(CollidableComponent.class).setValue(false);

        FXGL.inc("multiplier", +1);

        FXGL.animationBuilder()
                .duration(Duration.seconds(0.75))
                .onFinished(crystal::removeFromWorld)
                .interpolator(Interpolators.BACK.EASE_IN())
                .scale(crystal)
                .from(new Point2D(crystal.getScaleX(), crystal.getScaleY()))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }
}
