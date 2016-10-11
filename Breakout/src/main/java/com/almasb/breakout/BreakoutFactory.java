package com.almasb.breakout;

import com.almasb.breakout.control.BallControl;
import com.almasb.breakout.control.BatControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.parser.EntityFactory;
import com.almasb.fxgl.parser.EntityProducer;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.gameutils.math.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BreakoutFactory extends EntityFactory {

    public BreakoutFactory() {
        super('0');
    }

    @EntityProducer('1')
    public Entity newBrick(int x, int y) {
        return Entities.builder()
                .type(EntityType.BRICK)
                .at(x * 40, y * 40)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("brick.jpg", 60, 30))
                .with(new PhysicsComponent(), new CollidableComponent(true))
                .build();
    }

    @EntityProducer('9')
    public Entity newBat(int x, int y) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return Entities.builder()
                .type(EntityType.BAT)
                .at(FXGL.getSettings().getWidth() / 2 - 50, 30)
                .viewFromNodeWithBBox(FXGL.getAssetLoader().loadTexture("bat.jpg", 100, 30))
                .with(physics, new CollidableComponent(true))
                .with(new BatControl())
                .build();
    }

    @EntityProducer('2')
    public Entity newBall(int x, int y) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setOnPhysicsInitialized(() -> physics.setBodyLinearVelocity(new Vec2(5, 5)));

        FixtureDef fd = new FixtureDef();
        fd.setRestitution(1f);
        fd.setDensity(0.03f);
        physics.setFixtureDef(fd);

        return Entities.builder()
                .type(EntityType.BALL)
                .at(x * 40, y * 40)
                .bbox(new HitBox("Main", BoundingShape.circle(20)))
                .viewFromNode(FXGL.getAssetLoader().loadTexture("ball.png", 40, 40))
                .with(physics, new CollidableComponent(true))
                .with(new BallControl())
                .build();
    }
}
