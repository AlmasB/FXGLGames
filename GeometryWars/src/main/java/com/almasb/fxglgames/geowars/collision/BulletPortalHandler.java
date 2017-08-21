package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.annotation.AddCollisionHandler;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.GeoWarsType;
import com.almasb.fxglgames.geowars.component.BulletComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.inc;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@AddCollisionHandler
public class BulletPortalHandler extends CollisionHandler {

    public BulletPortalHandler() {
        super(GeoWarsType.BULLET, GeoWarsType.PORTAL);
    }

    @Override
    protected void onCollisionBegin(Entity b, Entity portal) {
        GameEntity bullet = (GameEntity) b;
        BulletComponent component = bullet.getComponent(BulletComponent.class);

        if (component.getLastPortal() != portal) {
            bullet.getWorld()
                    .getEntitiesByType(GeoWarsType.PORTAL)
                    .stream()
                    .filter(e -> e != portal)
                    .findAny()
                    .map(e -> (GameEntity) e)
                    .ifPresent(anotherPortal -> {
                        component.setLastPortal(anotherPortal);
                        bullet.setPosition(anotherPortal.getCenter());
                        bullet.getControl(ProjectileControl.class).setDirection(FXGLMath.randomPoint2D());
                    });
        }
    }
}
