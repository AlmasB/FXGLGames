package com.almasb.fxglgames.gravity.old;

import com.almasb.fxgl.core.math.Vec2;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import org.jbox2d.dynamics.BodyType;

public class Enemy extends GameObject {

    public enum Direction {
        LEFT, RIGHT
    }

    private Direction direction = Direction.LEFT;

    private int tick = 0;
    private int frame = 0, place = 0;

    int FACTOR = 15;

    private boolean isManipulated = false;

    private ImageView sprite;

    private int hp = 4;

    public Enemy(float x, float y) {
        super(x, y, Config.BLOCK_SIZE - 2, Config.BLOCK_SIZE - 2, BodyType.DYNAMIC, true);

        sprite = new ImageView(Config.Images.ENEMY);
        sprite.setFitHeight(Config.BLOCK_SIZE);
        sprite.setFitWidth(Config.BLOCK_SIZE);
        sprite.setViewport(new Rectangle2D(0, 120, 40, 40));

        getChildren().add(sprite);
    }

    @Override
    public void onUpdate() {
        if (dying) {
            if (!bodyDestroyed) {
                GameEnvironment.getWorld().destroyBody(body);
                bodyDestroyed = true;
            }
            return;
        }

        if (Math.round(this.getRotate()) != 0) {
            isManipulated = true;
        }

        if (!body.isAwake() && Math.round(this.getRotate()) == 0) {
            isManipulated = false;
        }

        if (!isManipulated) {
            this.body.setLinearVelocity((new Vec2(direction == Direction.LEFT ? -3 : 3, 0)));

            tick++;
            if (tick == 2 * Config.SECOND) {
                shoot(true);
                direction = Direction.RIGHT;
            }
            else if (tick == 4 * Config.SECOND) {
                shoot(false);
                direction = Direction.LEFT;
                tick = 0;
            }

            frame++;

            if (frame == 4 * FACTOR)
                frame = 0;

            if (frame / FACTOR == 0 || frame / FACTOR == 2)
                place = 0;
            if (frame / FACTOR == 1)
                place = 1;
            if (frame / FACTOR == 3)
                place = 2;
        }

        sprite.setViewport(new Rectangle2D(place * 40, 80 + 40 * direction.ordinal(), 40, 40));
    }

    @Override
    public void onDeath() {
        dying = true;

        sprite.setImage(Config.Images.EXPLOSION);
        sprite.setViewport(new Rectangle2D(0, 0, 40, 40));

        SimpleIntegerProperty frameProperty = new SimpleIntegerProperty();
        frameProperty.addListener((obs, old, newValue) -> {
            if (newValue.intValue() > old.intValue())
                sprite.setViewport(new Rectangle2D((newValue.intValue() % 5) * 40, (newValue.intValue() / 5) * 40, 40, 40));
        });

        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(frameProperty, 24)));
        t.setOnFinished(event -> {
            alive = false;
        });
        t.play();

        Config.Audio.EXPLOSION.play();
    }

    private void shoot(boolean left) {
        Vec2 v = GameEnvironment.getPlayer().body.getPosition();

        Vec2 dir = v.sub(body.getPosition());
        Vec2 bulletV = new Vec2();

        if (dir.x > 0 && direction == Direction.LEFT) {
            bulletV.set(-25, 0);
        }

        if (dir.x < 0 && direction == Direction.LEFT) {
            bulletV.set(dir);
        }

        if (dir.x < 0 && direction == Direction.RIGHT) {
            bulletV.set(25, 0);
        }

        if (dir.x > 0 && direction == Direction.RIGHT) {
            bulletV.set(dir);
        }

//        Bullet b = new Bullet((float)getTranslateX() + (left ? -10 : Config.BLOCK_SIZE + 2), (float)getTranslateY() + Config.BLOCK_SIZE / 2,
//                bulletV);
//        GameEnvironment.addObject(b);
    }

    public void setUnstable() {
        isManipulated = true;
    }

    @Override
    public Type getType() {
        return Type.ENEMY;
    }

    @Override
    public void onCollide(GameObject other) {
        if (other.getType() == Type.PLATFORM) {
            if (body.getLinearVelocity().abs().x > 50 || body.getLinearVelocity().y > 50) {
                hp -= 2;
                if (hp <= 0)
                    onDeath();
            }
        }

        if (other.getType() == Type.STONE) {
            if (other.body.getLinearVelocity().abs().x > 20 || other.body.getLinearVelocity().y > 20) {
                hp -= 2;
                if (hp <= 0)
                    onDeath();
            }
        }

        if (other.getType() == Type.BULLET) {
            hp -= 2;
            if (hp <= 0)
                onDeath();
        }
    }
}
