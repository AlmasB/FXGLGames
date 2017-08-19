package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.inc;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class PlayerCrystalHandler extends CollisionHandler {

    public PlayerCrystalHandler() {
        super(GeoWarsType.PLAYER, GeoWarsType.CRYSTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity crystal) {
        crystal.getComponent(CollidableComponent.class).setValue(false);

        inc("multiplier", +1);

        Entities.animationBuilder()
                .duration(Duration.seconds(0.75))
                .onFinished(crystal::removeFromWorld)
                .interpolator(Interpolators.BACK.EASE_IN())
                .scale((GameEntity) crystal)
                .from(new Point2D(0.75, 0.75))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }
}
