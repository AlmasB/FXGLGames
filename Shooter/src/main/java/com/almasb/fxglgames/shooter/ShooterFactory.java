package com.almasb.fxglgames.shooter;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ShooterFactory implements EntityFactory {

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(ShooterType.BULLET)
                .viewWithBBox(new Rectangle(10, 2, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(0, -1), 300))
                .build();
    }

    @Spawns("Enemy")
    public Entity newEnemy(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(ShooterType.ENEMY)
                .viewWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .build();
    }
}
