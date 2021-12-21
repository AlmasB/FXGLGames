package com.almasb.fxglgames.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.td.components.EnemyComponent;
import com.almasb.fxglgames.td.components.TowerComponent;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxglgames.td.EntityType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseFactory implements EntityFactory {

    @Spawns("Enemy")
    public Entity spawnEnemy(SpawnData data) {
        return entityBuilder(data)
                .type(ENEMY)
                .viewWithBBox("enemy.png")
                .collidable()
                .with(new EnemyComponent(data.get("way")))
                .build();
    }

    @Spawns("Tower")
    public Entity spawnTower(SpawnData data) {
        return entityBuilder(data)
                .type(TOWER)
                .view("tower.png")
                .collidable()
                .with(new TowerComponent())
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(new Rectangle(15, 5, Color.DARKGREY))
                .collidable()
                .with(new OffscreenCleanComponent())
                .build();
    }

    @Spawns("towerBase")
    public Entity newTowerBase(SpawnData data) {
        var rect = new Rectangle(64, 64, Color.GREEN);
        rect.setOpacity(0.25);

        var cell = entityBuilder(data)
                .type(TOWER_BASE)
                .view(rect)
                .onClick(e -> {
                    FXGL.<TowerDefenseApp>getAppCast().onCellClicked(e);
                })
                .build();

        rect.fillProperty().bind(
                Bindings.when(cell.getViewComponent().getParent().hoverProperty())
                        .then(Color.DARKGREEN)
                        .otherwise(Color.GREEN)
        );

        return cell;
    }

    @Spawns("waypoint")
    public Entity newWaypoint(SpawnData data) {
        //Polygon polygon = data.get("polygon");

        return entityBuilder(data)
                .type(WAY)
                .build();
    }

    @Spawns("start")
    public Entity newStart(SpawnData data) {
        return entityBuilder(data)
                .build();
    }
}
