package com.almasb.geowars;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.geowars.component.OldPositionComponent;
import com.almasb.geowars.control.BulletControl;
import com.almasb.geowars.control.SeekerControl;
import com.almasb.geowars.control.WandererControl;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GeoWarsFactory {

    private GameWorld world;

    public GeoWarsFactory(GameWorld world) {
        this.world = world;
    }

    public GameEntity spawnPlayer() {
        // TODO: move this to proper PlayerControl
        OldPositionComponent oldPosition = new OldPositionComponent();
        oldPosition.valueProperty().addListener((obs, old, newPos) -> {
            Entities.getRotation(oldPosition.getEntity()).rotateToVector(newPos.subtract(old));
        });

        return Entities.builder()
                .type(EntityType.PLAYER)
                .at(FXGL.getApp().getWidth() / 2, FXGL.getApp().getHeight() / 2)
                .viewFromTextureWithBBox("Player.png")
                .with(new CollidableComponent(true), oldPosition)
                .buildAndAttach(world);
    }

    public GameEntity spawnBullet(Point2D position, Point2D direction) {
        FXGL.getAudioPlayer().playSound("shoot" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .type(EntityType.BULLET)
                .at(position)
                .viewFromTextureWithBBox("Bullet.png")
                .with(new CollidableComponent(true))
                .with(new ProjectileControl(direction, 600),
                        new BulletControl(FXGL.<GeoWarsApp>getAppCast().getGrid()),
                        new OffscreenCleanControl())
                .buildAndAttach(world);
    }

    public GameEntity spawnWanderer(double x, double y) {
        return Entities.builder()
                .type(EntityType.WANDERER)
                .at(x, y)
                .viewFromTextureWithBBox("Wanderer.png")
                .with(new CollidableComponent(true))
                .with(new WandererControl((int)FXGL.getApp().getWidth(), (int)FXGL.getApp().getHeight()))
                .buildAndAttach(world);
    }

    public GameEntity spawnSeeker(double x, double y) {
        return Entities.builder()
                .type(EntityType.SEEKER)
                .at(x, y)
                .viewFromTextureWithBBox("Seeker.png")
                .with(new CollidableComponent(true))
                .with(new SeekerControl(FXGL.<GeoWarsApp>getAppCast().getPlayer()))
                .buildAndAttach(world);
    }

    public GameEntity spawnExplosion(Point2D point) {
        FXGL.getAudioPlayer().playSound("explosion-0" + (int) (Math.random() * 8 + 1) + ".wav");

        return Entities.builder()
                .at(point.subtract(40, 40))
                .viewFromNode(FXGL.getAssetLoader().loadTexture("explosion.png", 80 * 48, 80).toAnimatedTexture(48, Duration.seconds(2)))
                .with(new ExpireCleanControl(Duration.seconds(1.8)))
                .buildAndAttach(world);
    }
}
