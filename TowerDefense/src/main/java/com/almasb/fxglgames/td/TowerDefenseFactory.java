package com.almasb.fxglgames.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.td.components.BulletComponent;
import com.almasb.fxglgames.td.components.EnemyComponent;
import com.almasb.fxglgames.td.components.EnemyHealthViewComponent;
import com.almasb.fxglgames.td.components.TowerComponent;
import com.almasb.fxglgames.td.data.EnemyData;
import com.almasb.fxglgames.td.data.TowerData;
import com.almasb.fxglgames.td.ui.PowerDownIcon;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxglgames.td.EntityType.*;
import static com.almasb.fxglgames.td.data.Config.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseFactory implements EntityFactory {

    @Spawns("Enemy")
    public Entity spawnEnemy(SpawnData data) {
        EnemyData enemyData = data.get("enemyData");

        return entityBuilder(data)
                .type(ENEMY)
                .viewWithBBox("enemies/" + enemyData.imageName())
                .collidable()
                .with(new TimeComponent())
                .with(new EffectComponent())
                .with(new HealthIntComponent(enemyData.hp()))
                .with(new EnemyComponent(data.get("way"), enemyData))
                .with(new AutoRotationComponent())
                .with(new EnemyHealthViewComponent())
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
        return entityBuilder(data)
                .type(WAY)
                .build();
    }

    // VISUAL EFFECTS

    @Spawns("visualEffectSlow")
    public Entity newVisualEffectSlow(SpawnData data) {
        var icon1 = new PowerDownIcon(Color.YELLOW);
        var icon2 = new PowerDownIcon(Color.ORANGE);

        var icon3 = new PowerDownIcon(Color.YELLOW);
        var icon4 = new PowerDownIcon(Color.ORANGE);

        var box = new VBox(-5, icon3, icon4);
        box.setTranslateX(64.0);
        box.setTranslateY(-25.0);

        return entityBuilder(data)
                .viewWithBBox(new VBox(-5, icon1, icon2))
                .viewWithBBox(box)
                .scale(0.3, 0.3)
                .build();
    }
}
