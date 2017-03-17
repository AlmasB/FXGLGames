package com.almasb.td;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.td.control.EnemyControl;
import com.almasb.td.control.TowerControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class TowerDefenseFactory implements EntityFactory {

    @Spawns("Enemy")
    public GameEntity spawnEnemy(SpawnData data) {
        return Entities.builder()
                .type(TowerDefenseType.ENEMY)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }

    @Spawns("Tower")
    public GameEntity spawnTower(SpawnData data) {
        return Entities.builder()
                //.type(TowerDefenseType.ENEMY)
                .from(data)
                .viewFromNode(new Rectangle(40, 40, Color.BLACK))
                //.with(new CollidableComponent(true))
                .with(new TowerControl())
                .build();
    }

    @Spawns("Bullet")
    public GameEntity spawnBullet(SpawnData data) {
        return Entities.builder()
                .type(TowerDefenseType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(15, 5, Color.DARKGREY))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanControl())
                .build();
    }
}
