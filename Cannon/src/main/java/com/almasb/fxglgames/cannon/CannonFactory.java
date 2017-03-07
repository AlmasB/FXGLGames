package com.almasb.fxglgames.cannon;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.LiftControl;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class CannonFactory implements EntityFactory {

    @Spawns("cannon")
    public Entity newCannon(SpawnData data) {
        return Entities.builder()
                .type(CannonType.CANNON)
                .from(data)
                .viewFromNode(new Rectangle(70, 30, Color.BROWN))
                .with(new LiftControl(Duration.seconds(1), 150, true))
                .build();
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.05f);

        physics.setFixtureDef(fd);
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setOnPhysicsInitialized(() -> {
            Point2D mousePosition = FXGL.getInput().getMousePositionWorld();

            physics.setLinearVelocity(mousePosition.subtract(data.getX(), data.getY()).normalize().multiply(800));
        });

        return Entities.builder()
                .type(CannonType.BULLET)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(25, 25, Color.BLUE))
                .with(physics, new CollidableComponent(true))
                .with(new ExpireCleanControl(Duration.seconds(4)))
                .build();
    }

    @Spawns("basketBarrier")
    public Entity newBasketBarrier(SpawnData data) {
        return Entities.builder()
                .type(CannonType.BASKET)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(100, 300, Color.RED))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("basketGround")
    public Entity newBasketGround(SpawnData data) {
        return Entities.builder()
                .type(CannonType.BASKET)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(300, 5, Color.TRANSPARENT))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();
    }
}
