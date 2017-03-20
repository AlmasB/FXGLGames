package com.almasb.geowars;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.geowars.component.OldPositionComponent;
import com.almasb.geowars.control.*;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class GeoWarsFactory implements EntityFactory {

    private Point2D[] spawnPoints = new Point2D[] {
            new Point2D(50, 50),
            new Point2D(FXGL.getApp().getWidth() - 50, 50),
            new Point2D(FXGL.getApp().getWidth() - 50, FXGL.getApp().getHeight() - 50),
            new Point2D(50, FXGL.getApp().getHeight() - 50)
    };

    private GameWorld world;

    public GeoWarsFactory() {
        this.world = FXGL.getApp().getGameWorld();
    }

    private Point2D getRandomSpawnPoint() {
        return spawnPoints[FXGLMath.random(3)];
    }

    @Spawns("Player")
    public GameEntity spawnPlayer(SpawnData data) {
        // TODO: move this to proper PlayerControl
        OldPositionComponent oldPosition = new OldPositionComponent();
        oldPosition.valueProperty().addListener((obs, old, newPos) -> {
            Entities.getRotation(oldPosition.getEntity()).rotateToVector(newPos.subtract(old));
        });

        return Entities.builder()
                .type(GeoWarsType.PLAYER)
                .at(FXGL.getApp().getWidth() / 2, FXGL.getApp().getHeight() / 2)
                .viewFromTextureWithBBox("Player.png")
                .with(new CollidableComponent(true), oldPosition)
                .with(new PlayerControl(), new KeepOnScreenControl(true, true))
                .build();
    }

    public GameEntity spawnBullet(Point2D position, Point2D direction) {
        FXGL.getAudioPlayer().playSound("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .type(GeoWarsType.BULLET)
                .at(position)
                .viewFromTextureWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(direction, 600),
                        new BulletControl(FXGL.<GeoWarsApp>getAppCast().getGrid()),
                        new OffscreenCleanControl())
                .buildAndAttach(world);
    }

    @Spawns("Wanderer")
    public GameEntity spawnWanderer(SpawnData data) {
        return Entities.builder()
                .type(GeoWarsType.WANDERER)
                .at(getRandomSpawnPoint())
                .viewFromTextureWithBBox("Wanderer.png")
                .with(new CollidableComponent(true))
                .with(new WandererControl())
                .build();
    }

    @Spawns("Seeker")
    public GameEntity spawnSeeker(SpawnData data) {
        return Entities.builder()
                .type(GeoWarsType.SEEKER)
                .at(getRandomSpawnPoint())
                .viewFromTextureWithBBox("Seeker.png")
                .with(new CollidableComponent(true))
                .with(new SeekerControl(FXGL.<GeoWarsApp>getAppCast().getPlayer()))
                .build();
    }

    @Spawns("Explosion")
    public GameEntity spawnExplosion(SpawnData data) {
        FXGL.getAudioPlayer().playSound("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .at(data.getX() - 40, data.getY() - 40)
                .viewFromNode(FXGL.getAssetLoader().loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
                .with(new ExpireCleanControl(Duration.seconds(1.8)))
                .build();
    }
}
