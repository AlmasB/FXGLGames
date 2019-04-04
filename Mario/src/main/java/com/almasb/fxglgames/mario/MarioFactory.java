package com.almasb.fxglgames.mario;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioFactory implements EntityFactory {

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        return FXGL.entityBuilder()
                .type(MarioType.ENEMY)
                .from(data)
                .viewWithBBox(new Rectangle(30, 30, Color.RED))
                .with(physics)
                .with(new EnemyControl())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return FXGL.entityBuilder()
                .type(MarioType.PLATFORM)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("door")
    public Entity newDoor(SpawnData data) {
        return FXGL.entityBuilder()
                .type(MarioType.DOOR)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        return FXGL.entityBuilder()
                .type(MarioType.PLAYER)
                .from(data)
                .bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)))
                .bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)))
                //.bbox(new HitBox(BoundingShape.box(32, 42)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerControl())
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        return FXGL.entityBuilder()
                .type(MarioType.COIN)
                .from(data)
                .viewWithBBox(new Circle(data.<Integer>get("width") / 2, Color.GOLD))
                .with(new CollidableComponent(true))
                .build();
    }
}
