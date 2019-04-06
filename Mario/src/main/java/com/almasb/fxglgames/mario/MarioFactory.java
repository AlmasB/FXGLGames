package com.almasb.fxglgames.mario;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxglgames.mario.MarioType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return FXGL.entityBuilder()
                .type(PLATFORM)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("exitTrigger")
    public Entity newExitTrigger(SpawnData data) {
        return FXGL.entityBuilder()
                .type(EXIT_TRIGGER)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("doorTop")
    public Entity newDoorTop(SpawnData data) {
        var door = FXGL.entityBuilder()
                .type(DOOR_TOP)
                .from(data)
                .build();

        door.getViewComponent().opacityProperty().setValue(0);

        return door;
    }

    @Spawns("doorBot")
    public Entity newDoorBot(SpawnData data) {
        var door = FXGL.entityBuilder()
                .type(DOOR_BOT)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(false))
                .build();

        door.getViewComponent().opacityProperty().setValue(0);

        return door;
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(16, 38), BoundingShape.box(6, 8)));

        return FXGL.entityBuilder()
                .type(PLAYER)
                .from(data)
                .bbox(new HitBox(new Point2D(5,5), BoundingShape.circle(12)))
                .bbox(new HitBox(new Point2D(10,25), BoundingShape.box(10, 17)))
                //.bbox(new HitBox(BoundingShape.box(32, 42)))
                .with(physics)
                .with(new CollidableComponent(true))
                .with(new IrremovableComponent())
                .with(new PlayerComponent())
                .build();
    }

    @Spawns("exitSign")
    public Entity newExit(SpawnData data) {
        return FXGL.entityBuilder()
                .type(EXIT_SIGN)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("keyPrompt")
    public Entity newPrompt(SpawnData data) {
        return FXGL.entityBuilder()
                .type(KEY_PROMPT)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("keyCode")
    public Entity newKeyCode(SpawnData data) {
        String key = data.get("key");

        KeyCode keyCode = KeyCode.getKeyCode(key);

        return FXGL.entityBuilder()
                .from(data)
                .view(new KeyView(keyCode, Color.YELLOW, 24))
                .with(new LiftComponent(Duration.seconds(0.76), 6, true))
                .zIndex(100)
                .build();
    }

    @Spawns("button")
    public Entity newButton(SpawnData data) {
        var keyEntity = FXGL.getGameWorld().create("keyCode", new SpawnData(data.getX(), data.getY() - 50).put("key", "E"));
        keyEntity.getViewComponent().opacityProperty().setValue(0);

        return FXGL.entityBuilder()
                .type(BUTTON)
                .from(data)
                .viewWithBBox(new Rectangle(20, 20, Color.GREEN))
                .with(new CollidableComponent(true))
                .with("keyEntity", keyEntity)
                .build();
    }
}
