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
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.ui.FontType;
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
        return FXGL.entityBuilder()
                .type(DOOR_TOP)
                .from(data)
                .opacity(0)
                .build();
    }

    @Spawns("doorBot")
    public Entity newDoorBot(SpawnData data) {
        return FXGL.entityBuilder()
                .type(DOOR_BOT)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .opacity(0)
                .with(new CollidableComponent(false))
                .build();
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

    @Spawns("enemyBox")
    public Entity newEnemyBox(SpawnData data) {
        return FXGL.entityBuilder()
                .type(ENEMY)
                .from(data)
                .bbox(new HitBox(new Point2D(10, 10), BoundingShape.box(data.<Integer>get("width") - 20, data.<Integer>get("height") - 20)))
                .with(new EnemyBoxComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("messagePrompt")
    public Entity newMessagePrompt(SpawnData data) {
        var text = FXGL.getUIFactory().newText(data.get("message"), Color.BLACK, 14.0);
        text.setFont(FXGL.getUIFactory().newFont(FontType.GAME, 20.0));
        text.setStrokeWidth(2);

        return FXGL.entityBuilder()
                .type(MESSAGE_PROMPT)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .view(text)
                .with(new CollidableComponent(true))
                .opacity(0)
                .build();
    }

    @Spawns("portal")
    public Entity newPortal(SpawnData data) {
        return FXGL.entityBuilder()
                .type(PORTAL)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PortalComponent())
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("lift")
    public Entity newLift(SpawnData data) {
        var physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        boolean isGoingUp = data.hasKey("up") ? data.get("up") : true;

        var distance = (isGoingUp) ? data.getY() - data.<Integer>get("endY") : data.<Integer>get("endY") - data.getY();
        var speed = 100;
        var duration = Duration.seconds(distance / speed);

        return FXGL.entityBuilder()
                .type(LIFT)
                .from(data)
                .bbox(new HitBox(new Point2D(0, 50), BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height") - 50)))
                .with(physics)
                .with(new PhysicsLiftComponent(duration, distance, isGoingUp))
                .build();
    }

    @Spawns("destrBox")
    public Entity newDestructibleBox(SpawnData data) {
        var comp = new DestructibleBoxComponent();

        var box = FXGL.entityBuilder()
                .type(PLATFORM)
                .from(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(comp)
                .build();

        box.getViewComponent().addClickListener(comp::explode);

        return box;
    }
}
