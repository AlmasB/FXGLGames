package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.geowars.WeaponType;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.newLocalTimer;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxgl.dsl.FXGLForKtKt.geto;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerComponent extends Component {

    private static final Duration WEAPON_DELAY = Duration.seconds(0.17);

    private Point2D oldPosition;

    private int playerSpeed;
    private double speed;

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
    }

    public void shoot(Point2D shootPoint) {
        if (weaponTimer.elapsed(WEAPON_DELAY)) {
            Point2D position = entity.getCenter().subtract(14, 4.5);
            Point2D vectorToMouse = shootPoint.subtract(position);

            WeaponType type = geto("weaponType");

            List<Entity> bullets = new ArrayList<>();

            switch (type) {
                case MIRROR:

                case RICOCHET:

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

            if (type == WeaponType.MIRROR) {

                bullets.addAll(
                        bullets.stream()
                                .map(b -> spawnBullet(position, vectorToMouse.multiply(-1)))
                                .collect(Collectors.toList())
                );

                // TODO: duplicate code
                bullets.forEach(bullet -> {
                    bullet.removeComponent(OffscreenCleanComponent.class);
                    bullet.addComponent(new RicochetComponent());
                });
            }

            if (type == WeaponType.RICOCHET) {
                bullets.forEach(bullet -> {
                    bullet.removeComponent(OffscreenCleanComponent.class);
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

    public void releaseShockwave() {
        spawn("Shockwave", entity.getCenter());
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
}
