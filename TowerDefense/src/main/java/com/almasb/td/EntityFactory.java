package com.almasb.td;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.td.control.EnemyControl;
import com.almasb.td.control.TowerControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EntityFactory {

    public static GameEntity spawnEnemy(double x, double y) {
        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }

    public static GameEntity spawnTower(double x, double y) {
        return Entities.builder()
                //.type(EntityType.ENEMY)
                .at(x, y)
                .viewFromNode(new Rectangle(40, 40, Color.BLACK))
                //.with(new CollidableComponent(true))
                .with(new TowerControl())
                .build();
    }

    public static GameEntity spawnBullet(double x, double y) {
        return Entities.builder()
                .type(EntityType.BULLET)
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(15, 5, Color.DARKGREY))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl())
                .build();
    }
}
