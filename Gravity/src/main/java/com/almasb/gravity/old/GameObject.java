package com.almasb.gravity.old;

import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

public abstract class GameObject extends Parent {

    public enum Type {
        PLAYER, PLATFORM, ENEMY, COIN, POWERUP, BULLET, SPIKE, STONE, PORTAL, OBSTACLE
    }

    private Rectangle bbox;

    protected FixtureDef fixtureDef;
    protected Fixture fixture;
    protected BodyDef bodyDef;
    protected Body body;

    protected boolean bodyDestroyed = false;

    protected boolean alive = true, dying = false;

    private boolean physicsSupported = false;
    protected float width, height;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param type
     *              only used if physics flag is true
     * @param physics
     *              flag for physics simulation, if true
     *              the physics engine will perform collisions etc
     *              if false operations need to be done manually
     *              typical non-physics objects are coin/powerup/portal,
     *              i.e. player interactable objects
     */
    public GameObject(float x, float y, float width, float height, BodyType type, boolean physics) {
        this.setTranslateX(x);
        this.setTranslateY(y);
        this.width = width;
        this.height = height;


        bbox = new Rectangle(width, height);
        bbox.setFill(null);
        bbox.setStroke(Color.BLUE);

        getChildren().add(bbox);

        if (physicsSupported = physics) {
//            fixtureDef = new FixtureDef();
//            PolygonShape shape = new PolygonShape();
//            shape.setAsBox(toMeters(width / 2), toMeters(height / 2));
//
//            fixtureDef.shape = shape;
//            fixtureDef.density = 0.005f;
//            fixtureDef.friction = 0.5f;
//
//            bodyDef = new BodyDef();
//            bodyDef.type = type;
//            bodyDef.position.set(toMeters(x + width / 2), toMeters(Config.APP_H - (y + height / 2)));
//
//            body = GameEnvironment.getWorld().createBody(bodyDef);
//            fixture = body.createFixture(fixtureDef);
//
//            body.setUserData(this);
        }
    }

    public void update() {
        if (physicsSupported) {
            bbox.setStroke(body.isAwake() ? Color.GOLD : Color.RED);

            this.setTranslateX(toPixels(body.getPosition().x - toMeters(bbox.getWidth() / 2)));
            this.setTranslateY(toPixels(toMeters(Config.APP_H) - body.getPosition().y - toMeters(bbox.getHeight() / 2)));
            this.setRotate(-Math.toDegrees(body.getAngle()));
        }
        onUpdate();
    }

    protected float toMeters(double pixels) {
        return (float)pixels * 0.05f;
    }

    protected float toPixels(double meters) {
        return (float)meters * 20f;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPhysicsSupported() {
        return physicsSupported;
    }

    public boolean isColliding(GameObject other) {
        return getTranslateX() + width >= other.getTranslateX() && getTranslateX() <= other.getTranslateX() + other.width
                && getTranslateY() + height >= other.getTranslateY() && getTranslateY() <= other.getTranslateY() + other.height;
    }

    protected abstract void onUpdate();
    public abstract void onDeath();
    public abstract void onCollide(GameObject other);
    public abstract Type getType();
}
