package com.almasb.fxglgames.cannon;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.LiftComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.cannon.EntityType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CannonFactory implements EntityFactory {

    @Spawns("cannon")
    public Entity newCannon(SpawnData data) {
        return entityBuilder(data)
                .type(CANNON)
                .view(new Rectangle(70, 30, Color.BROWN))
                .with(new LiftComponent().yAxisSpeedDuration(150, Duration.seconds(1)))
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(0.05f));
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setOnPhysicsInitialized(() -> {
            Point2D mousePosition = FXGL.getInput().getMousePositionWorld();

            physics.setLinearVelocity(mousePosition.subtract(data.getX(), data.getY()).normalize().multiply(800));
        });

        return entityBuilder(data)
                .type(BULLET)
                .viewWithBBox(new Rectangle(25, 25, Color.BLUE))
                .collidable()
                .with(physics)
                .with(new ExpireCleanComponent(Duration.seconds(4)))
                .build();
    }

    @Spawns("basketBarrier")
    public Entity newBasketBarrier(SpawnData data) {
        return entityBuilder(data)
                .type(BASKET)
                .viewWithBBox(new Rectangle(100, 300, Color.RED))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("basketGround")
    public Entity newBasketGround(SpawnData data) {
        return entityBuilder(data)
                .type(BASKET)
                .viewWithBBox(new Rectangle(300, 5, Color.TRANSPARENT))
                .collidable()
                .with(new PhysicsComponent())
                .build();
    }
}
