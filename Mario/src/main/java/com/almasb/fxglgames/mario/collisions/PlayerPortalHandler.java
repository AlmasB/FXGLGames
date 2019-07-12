package com.almasb.fxglgames.mario.collisions;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxglgames.mario.MarioType;
import com.almasb.fxglgames.mario.components.PortalComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.mario.MarioType.PORTAL;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerPortalHandler extends CollisionHandler {

    public PlayerPortalHandler() {
        super(MarioType.PLAYER, MarioType.PORTAL);
    }

    @Override
    protected void onCollisionBegin(Entity player, Entity portal) {
        var portal1 = portal.getComponent(PortalComponent.class);

        if (!portal1.isActive()) {
            return;
        }

        var portal2 = getGameWorld().getEntitiesByType(PORTAL)
                .stream()
                .filter(e -> e != portal)
                .findAny()
                .get()
                .getComponent(PortalComponent.class);

        portal2.activate();

        animationBuilder()
                .duration(Duration.seconds(0.5))
                .onFinished(() -> {
                    player.getComponent(PhysicsComponent.class).overwritePosition(portal2.getEntity().getPosition());

                    animationBuilder()
                            .scale(player)
                            .from(new Point2D(0, 0))
                            .to(new Point2D(1, 1))
                            .buildAndPlay();
                })
                .scale(player)
                .from(new Point2D(1, 1))
                .to(new Point2D(0, 0))
                .buildAndPlay();
    }
}
