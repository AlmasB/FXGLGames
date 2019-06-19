package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.geowars.WeaponType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGLForKtKt.geto;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Component {

    private static final Duration WEAPON_DELAY = Duration.seconds(0.17);

    private int playerSpeed;
    private double speed;

    private long spawnTime = System.currentTimeMillis();

    private LocalTimer weaponTimer = FXGL.newLocalTimer();

    public PlayerControl(int playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    @Override
    public void onAdded() {
        //entity.getView().setEffect(new Bloom());
    }

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * playerSpeed;
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
        return FXGL.getGameWorld().spawn("Bullet",
                new SpawnData(position.getX(), position.getY())
                        .put("direction", direction)
        );
    }

    public void releaseShockwave() {
        FXGL.spawn("Shockwave", entity.getCenter());
    }

    public void left() {
        entity.translateX(-speed);
        makeExhaustFire();
    }

    public void right() {
        entity.translateX(speed);
        makeExhaustFire();
    }

    public void up() {
        entity.translateY(-speed);
        makeExhaustFire();
    }

    public void down() {
        entity.translateY(speed);
        makeExhaustFire();
    }

    private void makeExhaustFire() {
        Vec2 position = new Vec2(entity.getCenter().getX(), entity.getCenter().getY());
        double rotation = entity.getRotation();

        Color midColor = Color.BLUE;
        Color sideColor = Color.MEDIUMVIOLETRED.brighter();

        Vec2 direction = Vec2.fromAngle(rotation);

        float t = (System.currentTimeMillis() - spawnTime) / 1000f;

        Vec2 baseVel = direction.mul(-45f);
        Vec2 perpVel = new Vec2(baseVel.y, -baseVel.x).mulLocal(2f * FXGLMath.sin(t * 10f));

        // subtract half extent x, y of Glow.png                                            mul half extent entity
        Vec2 pos = position.sub(new Vec2(17.5, 10)).addLocal(direction.negate().normalizeLocal().mulLocal(20));

        // middle stream
        Vec2 randVec = Vec2.fromAngle(FXGLMath.toDegrees(FXGLMath.random() * FXGLMath.PI2));
        Vec2 velMid = baseVel.add(randVec.mul(7.5f));

        FXGL.entityBuilder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velMid, 800, midColor))
                .buildAndAttach();

        // side streams
        Vec2 randVec1 = Vec2.fromAngle(FXGLMath.toDegrees(FXGLMath.random() * FXGLMath.PI2));
        Vec2 randVec2 = Vec2.fromAngle(FXGLMath.toDegrees(FXGLMath.random() * FXGLMath.PI2));

        Vec2 velSide1 = baseVel.add(randVec1.mulLocal(2.4f)).addLocal(perpVel);
        Vec2 velSide2 = baseVel.add(randVec2.mulLocal(2.4f)).subLocal(perpVel);

        FXGL.entityBuilder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velSide1, 800, sideColor))
                .buildAndAttach();

        FXGL.entityBuilder()
                .at(pos.x, pos.y)
                .with(new ExhaustParticleControl(velSide2, 800, sideColor))
                .buildAndAttach();

        // TODO: this is useless because vectors above created with "new"
        Pools.free(direction);
        Pools.free(position);
        Pools.free(baseVel);
        Pools.free(perpVel);
        Pools.free(pos);
        Pools.free(randVec);
        Pools.free(velMid);
        Pools.free(randVec1);
        Pools.free(randVec2);
        Pools.free(velSide1);
        Pools.free(velSide2);
    }
}
