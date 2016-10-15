package com.almasb.spacerunner;

import com.almasb.ents.Entity;
import com.almasb.ents.component.UserDataComponent;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.spacerunner.control.EnemyControl;
import com.almasb.spacerunner.control.KeepOnScreenControl;
import com.almasb.spacerunner.control.PlayerControl;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
public class SpaceRunnerFactory {

    public GameEntity newPlayer(double x, double y) {
        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_player.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new PlayerControl(), new KeepOnScreenControl(false, true))
                .build();
    }

    public GameEntity newBullet(double x, double y, EntityType ownerType) {
        return Entities.builder()
                .type(EntityType.BULLET)
                .at(x, y - 5.5)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_bullet.png", 22, 11))
                .with(new CollidableComponent(true), new UserDataComponent(ownerType))
                .with(new ProjectileControl(new Point2D(ownerType == EntityType.PLAYER ? 1 : -1, 0), 250),
                        new OffscreenCleanControl())
                .build();
    }

    public GameEntity newEnemy(double x, double y) {
        return Entities.builder()
                .type(EntityType.ENEMY)
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_enemy_1.png", 27, 33))
                .with(new CollidableComponent(true))
                .with(new EnemyControl())
                .build();
    }
}
