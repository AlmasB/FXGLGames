package com.almasb.fxglgames.td;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxglgames.td.control.EnemyControl;
import com.almasb.fxglgames.td.control.TowerControl;
import com.almasb.fxglgames.td.tower.TowerDataComponent;
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
        TowerDataComponent towerComponent;
        try {
            towerComponent = FXGL.getAssetLoader()
                    .loadKV("Tower" + data.get("index") + ".kv")
                    .to(TowerDataComponent.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse KV file: " + e);
        }

        return Entities.builder()
                .type(TowerDefenseType.TOWER)
                .from(data)
                .viewFromNode(new Rectangle(40, 40, data.get("color")))
                .with(new CollidableComponent(true), towerComponent)
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
