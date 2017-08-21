package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.EntityGroup;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxglgames.geowars.GeoWarsType;
import com.almasb.fxglgames.geowars.event.DeathEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Circle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShockwaveControl extends Control {

    private int radius = 10;
    private int maxRadius = FXGL.getAppWidth() / 2;

    private GameEntity shockwave;
    private Circle view;
    private EntityGroup<GameEntity> enemies;

    @Override
    public void onAdded(Entity entity) {
        shockwave = (GameEntity) entity;
        view = (Circle) shockwave.getView().getNodes().get(0);

        enemies = FXGL.getApp().getGameWorld().getGroup(
                GeoWarsType.WANDERER,
                GeoWarsType.SEEKER,
                GeoWarsType.RUNNER,
                GeoWarsType.BOUNCER
        );
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        radius += 350 * tpf;
        view.setRadius(radius);

        enemies.forEach(this::isInRadius, enemy -> {
            FXGL.getEventBus().fireEvent(new DeathEvent(enemy));
            enemy.removeFromWorld();
        });

        if (radius >= maxRadius) {
            entity.removeFromWorld();
        }
    }

    private boolean isInRadius(GameEntity enemy) {
        return enemy.isWithin(new Rectangle2D(
                shockwave.getX() - radius,
                shockwave.getY() - radius,
                radius * 2,
                radius * 2
        ));
    }
}
