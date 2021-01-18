package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import kotlin.Unit;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.GeoWarsType.BULLET;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RunnerComponent extends Component {

    private Vec2 moveVector = new Vec2();
    private int moveSpeed;
    private EntityGroup bullets;

    public RunnerComponent(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded() {
        bullets = getGameWorld().getGroup(BULLET);

        setRandomMoveVector();
    }

    @Override
    public void onUpdate(double tpf) {
        var movedVectors = new ArrayList<Point2D>();

        bullets.forEach(bullet -> {
            var vectorToRunner = entity.getCenter().subtract(bullet.getCenter());
            var bulletVector = bullet.getComponent(ProjectileComponent.class).getDirection();

            var angleBetweenVectors = vectorToRunner.angle(bulletVector);

            if (angleBetweenVectors <= 45) {
                var lineStart = bullet.getCenter();
                // calc how much space the bullet is covering in 60 frames
                var lineEnd = bullet.getCenter().add(bullet.getComponent(ProjectileComponent.class).getVelocity().multiply(tpf * 60));

                var d = lineEnd.subtract(lineStart).normalize();
                var p = entity.getCenter();

                var x = lineStart.add(d.multiply(p.subtract(lineStart).dotProduct(d)));

                // we will be colliding within 60 frames (1 sec)
                if (x.distance(lineStart) < lineEnd.distance(lineStart)) {
                    var vectorToFireLine = x.subtract(p);

                    // eventual collision
                    if (vectorToFireLine.magnitude() < 75) {
                        var vector = vectorToFireLine.multiply(-1)
                                .normalize()
                                .multiply(moveSpeed);

                        boolean notOk = movedVectors.stream()
                                .anyMatch(v -> v.angle(vector) > 35);

                        if (!notOk) {
                            movedVectors.add(vector);
                        } else {
                            if (!movedVectors.isEmpty()) {
                                movedVectors.add(movedVectors.get(movedVectors.size() - 1));
                            }
                        }
                    }
                }
            }

            return Unit.INSTANCE;
        });

        movedVectors.stream()
                .reduce((v1, v2) -> v1.add(v2))
                .ifPresent(v -> moveVector.set(v));

        checkBounds();

        entity.translate(moveVector.mul(tpf));
    }

    private void checkBounds() {
        if (entity.getX() < 0
                || entity.getY() < 0
                || entity.getRightX() >= getAppWidth()
                || entity.getBottomY() >= getAppHeight()) {
            setRandomMoveVector();
        }
    }

    private void setRandomMoveVector() {
        var newDirectionVector = FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()))
                .subtract(entity.getCenter());

        moveVector.set(newDirectionVector)
                .normalizeLocal()
                .mulLocal(moveSpeed);
    }
}
