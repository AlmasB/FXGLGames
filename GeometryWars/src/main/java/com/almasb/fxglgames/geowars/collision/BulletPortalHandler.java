package com.almasb.fxglgames.geowars.collision;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxglgames.geowars.component.BulletComponent;

import static com.almasb.fxglgames.geowars.GeoWarsType.BULLET;
import static com.almasb.fxglgames.geowars.GeoWarsType.PORTAL;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BulletPortalHandler extends CollisionHandler {

    public BulletPortalHandler() {
        super(BULLET, PORTAL);
    }

    @Override
    protected void onCollisionBegin(Entity bullet, Entity portal) {
        BulletComponent component = bullet.getComponent(BulletComponent.class);

        if (component.getLastPortal() != portal) {
            bullet.getWorld()
                    .getEntitiesByType(PORTAL)
                    .stream()
                    .filter(e -> e != portal)
                    .findAny()
                    .ifPresent(anotherPortal -> {
                        component.setLastPortal(anotherPortal);
                        bullet.setPosition(anotherPortal.getCenter());
                        bullet.getComponent(ProjectileComponent.class).setDirection(FXGLMath.randomPoint2D());
                    });
        }
    }
}
