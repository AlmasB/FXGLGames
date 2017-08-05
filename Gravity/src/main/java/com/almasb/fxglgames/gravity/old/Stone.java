package com.almasb.fxglgames.gravity.old;

import com.almasb.fxgl.core.math.Vec2;
import javafx.scene.image.ImageView;

import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

public class Stone extends GameObject {

    public Stone(float x, float y) {
        super(x, y, Config.BLOCK_SIZE, Config.BLOCK_SIZE, BodyType.DYNAMIC, true);

        ImageView sprite = new ImageView(Config.Images.STONE);
        sprite.setFitHeight(Config.BLOCK_SIZE);
        sprite.setFitWidth(Config.BLOCK_SIZE);

        getChildren().add(sprite);

        this.setOnMouseDragged(event -> {
            double x2 = -GameEnvironment.LEVEL_ROOT.getLayoutX() + event.getSceneX();
            double y2 = event.getSceneY();

            x2 /= Config.resolutionScale.get();
            y2 /= Config.resolutionScale.get();

            body.setLinearVelocity(new Vec2(toMeters(x2), toMeters(Config.APP_H - y2)).sub(body.getPosition()));
        });
    }

    @Override
    protected void onUpdate() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeath() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCollide(GameObject other) {
        // TODO Auto-generated method stub

    }

    @Override
    public Type getType() {
        return Type.STONE;
    }
}
