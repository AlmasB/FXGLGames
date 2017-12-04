package com.almasb.fxglgames.shooter;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ProjectileControl;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class ShooterFactory implements EntityFactory {

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ShooterType.BULLET)
                .viewFromNodeWithBBox(new Rectangle(10, 2, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(new Point2D(0, -1), 300))
                .build();
    }

    @Spawns("Enemy")
    public Entity newEnemy(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(ShooterType.ENEMY)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .build();
    }
}
