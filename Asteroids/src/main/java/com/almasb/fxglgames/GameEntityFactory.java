package com.almasb.fxglgames;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GameEntityFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .from(data)
                .view(new Rectangle(getAppWidth(), getAppHeight()))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder()
                .from(data)
                .viewWithBBox("player.png")
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("asteroid")
    public Entity newAsteroid(SpawnData data) {
        return entityBuilder()
                .type(EntityType.ASTEROID)
                .from(data)
                .viewWithBBox("asteroid.png")
                .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 100))
                .collidable()
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Point2D dir = data.get("dir");

        return entityBuilder()
                .type(EntityType.BULLET)
                .from(data)
                .viewWithBBox("bullet.png")
                .with(new ProjectileComponent(dir, 500))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }
}
