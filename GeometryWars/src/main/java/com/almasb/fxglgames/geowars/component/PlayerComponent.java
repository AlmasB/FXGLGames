package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.geowars.WeaponType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geto;
import static com.almasb.fxglgames.geowars.Config.WEAPON_DELAY;
import static com.almasb.fxglgames.geowars.GeoWarsType.GRID;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private Point2D oldPosition;

    private int playerSpeed;
    private double speed;

    private boolean isShockwaveReady = true;

    private LocalTimer weaponTimer = newLocalTimer();

    public PlayerComponent(int playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    @Override
    public void onAdded() {
        oldPosition = entity.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * playerSpeed;

        if (!entity.getPosition().equals(oldPosition))
            entity.rotateToVector(entity.getPosition().subtract(oldPosition));

        oldPosition = entity.getPosition();

        // TODO: extract to KeepInBoundsComponent
        
        var viewport = new Rectangle2D(0, 0, getAppWidth(), getAppHeight());

        if (getEntity().getX() < viewport.getMinX()) {
            getEntity().setX(viewport.getMinX());
        } else if (getEntity().getRightX() > viewport.getMaxX()) {
            getEntity().setX(viewport.getMaxX() - getEntity().getWidth());
        }

        if (getEntity().getY() < viewport.getMinY()) {
            getEntity().setY(viewport.getMinY());
        } else if (getEntity().getBottomY() > viewport.getMaxY()) {
            getEntity().setY(viewport.getMaxY() - getEntity().getHeight());
        }
    }

    public void shoot(Point2D shootPoint) {
        if (weaponTimer.elapsed(WEAPON_DELAY)) {
            Point2D position = entity.getCenter().subtract(14, 4.5);
            Point2D vectorToMouse = shootPoint.subtract(position);

            WeaponType type = geto("weaponType");

            List<Entity> bullets = new ArrayList<>();

            switch (type) {
                case TRIPLE:

                    // spawn extra bullet
                    bullets.add(spawnBullet(position.subtract(
                            new Point2D(vectorToMouse.getY(), -vectorToMouse.getX()).normalize().multiply(15)
                    ), vectorToMouse));

                case DOUBLE:

                    // spawn extra bullet
                    bullets.add(spawnBullet(position.add(
                            new Point2D(vectorToMouse.getY(), -vectorToMouse.getX()).normalize().multiply(15)
                    ), vectorToMouse));

                case SINGLE:
                default:
                    bullets.add(spawnBullet(position, vectorToMouse));
                    break;
            }

            if (getb("isRicochet")) {
                bullets.forEach(bullet -> {
                    bullet.addComponent(new RicochetComponent());
                });
            }

            weaponTimer.capture();
        }
    }

    private Entity spawnBullet(Point2D position, Point2D direction) {
        return spawn("Bullet",
                new SpawnData(position.getX(), position.getY())
                        .put("direction", direction)
        );
    }

    public boolean isShockwaveReady() {
        return isShockwaveReady;
    }

    public void setShockwaveReady(boolean shockwaveReady) {
        isShockwaveReady = shockwaveReady;
    }

    public void releaseShockwave() {
        if (isShockwaveReady) {
            isShockwaveReady = false;
            spawn("Shockwave", entity.getCenter());
        }
    }

    public void left() {
        entity.translateX(-speed);
    }

    public void right() {
        entity.translateX(speed);
    }

    public void up() {
        entity.translateY(-speed);
    }

    public void down() {
        entity.translateY(speed);
    }

    public void playSpawnAnimation() {
        for (int i = 0; i < 6; i++) {
            final int j = i;

            runOnce(() -> {
                byType(GRID).get(0).getComponent(GridComponent.class)
                        .applyExplosiveForce(1500 + j*100, new Point2D(getAppWidth() / 2.0, getAppHeight() / 2.0), j*50 + 50);
            }, Duration.seconds(i * 0.4));
        }

        var emitter = ParticleEmitters.newExplosionEmitter(450);
        emitter.setSize(1, 16);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setStartColor(Color.color(1.0, 1.0, 1.0, 0.5));
        emitter.setEndColor(Color.BLUE);
        emitter.setMaxEmissions(20);
        emitter.setEmissionRate(0.5);

        entityBuilder()
                .at(entity.getPosition())
                .with(new ParticleComponent(emitter))
                .with(new ExpireCleanComponent(Duration.seconds(3)))
                .buildAndAttach();

        animationBuilder()
                .fadeIn(entity)
                .buildAndPlay();
    }
}
