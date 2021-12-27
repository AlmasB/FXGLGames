package com.almasb.fxglgames.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.td.components.BulletComponent;
import com.almasb.fxglgames.td.components.EnemyComponent;
import com.almasb.fxglgames.td.components.TowerComponent;
import com.almasb.fxglgames.td.data.Config;
import com.almasb.fxglgames.td.data.EnemyData;
import com.almasb.fxglgames.td.data.TowerData;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.td.EntityType.*;
import static com.almasb.fxglgames.td.data.Config.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseFactory implements EntityFactory {

    @Spawns("Enemy")
    public Entity spawnEnemy(SpawnData data) {
        EnemyData enemyData = data.get("enemyData");

        var hp = new HealthIntComponent(enemyData.hp());

        var hpBar = new ProgressBar(false);
        hpBar.setTranslateY(64);
        hpBar.setWidth(60);
        hpBar.setHeight(10);
        hpBar.setFill(Color.LIGHTGREEN);
        hpBar.setLabelVisible(false);
        hpBar.maxValueProperty().bind(hp.maxValueProperty());
        hpBar.currentValueProperty().bind(hp.valueProperty());

        return entityBuilder(data)
                .type(ENEMY)
                .viewWithBBox("enemy.png")
                .view(hpBar)
                .collidable()
                .with(hp)
                .with(new EnemyComponent(data.get("way"), enemyData))
                .build();
    }

    @Spawns("Tower")
    public Entity spawnTower(SpawnData data) {
        TowerData towerData = data.get("towerData");

        return entityBuilder(data)
                .type(TOWER)
                .viewWithBBox(towerData.imageName())
                .collidable()
                .with(new TowerComponent(towerData))
                .zIndex(Z_INDEX_TOWER)
                .build();
    }

    @Spawns("Bullet")
    public Entity spawnBullet(SpawnData data) {
        String imageName = data.get("imageName");

        Node view = texture(imageName);
        view.setRotate(90);

        Entity tower = data.get("tower");
        Entity target = data.get("target");

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(view)
                .collidable()
                .with(new BulletComponent(tower, target))
                .with(new AutoRotationComponent())
                .zIndex(Z_INDEX_BULLET)
                .build();
    }

    @Spawns("towerBase")
    public Entity newTowerBase(SpawnData data) {
        var rect = new Rectangle(64, 64, Color.GREEN);
        rect.setOpacity(0.25);

        var cell = entityBuilder(data)
                .type(TOWER_BASE)
                .viewWithBBox(rect)
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
