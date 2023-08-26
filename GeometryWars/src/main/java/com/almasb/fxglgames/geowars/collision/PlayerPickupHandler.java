package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.component.PickupComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.PICKUP_CRYSTAL;
import static com.almasb.fxglgames.geowars.GeoWarsType.PLAYER;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerPickupHandler extends CollisionHandler {

    public PlayerPickupHandler() {
        super(PLAYER, PICKUP_CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity pickup) {
        pickup.getComponent(CollidableComponent.class).setValue(false);

        pickup.getComponent(PickupComponent.class).pickUp();

        animationBuilder()
                .duration(Duration.seconds(0.75))
                .onFinished(pickup::removeFromWorld)
                .interpolator(Interpolators.ELASTIC.EASE_IN())
                .scale(pickup)
                .from(new Point2D(pickup.getScaleX(), pickup.getScaleY()))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }
}
