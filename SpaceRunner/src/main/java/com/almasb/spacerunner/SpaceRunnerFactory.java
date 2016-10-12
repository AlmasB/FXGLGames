package com.almasb.spacerunner;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.spacerunner.control.PlayerControl;
import com.google.inject.Singleton;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
public class SpaceRunnerFactory {

    public Entity newPlayer(double x, double y) {
        return Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_player.png", 40, 40))
                .with(new PlayerControl())
                .build();
    }

    public GameEntity newBullet(double x, double y) {
        return Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("sprite_bullet.png", 22, 11))
                .with(new ProjectileControl(new Point2D(1, 0), 250))
                .build();
    }
}
