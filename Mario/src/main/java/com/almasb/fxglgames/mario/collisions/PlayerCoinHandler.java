package com.almasb.fxglgames.mario.collisions;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxglgames.mario.MarioType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerCoinHandler extends CollisionHandler {

    public PlayerCoinHandler() {
        super(MarioType.PLAYER, MarioType.COIN);
    }

    @Override
    protected void onCollision(Entity player, Entity coin) {
        coin.removeComponent(CollidableComponent.class);

        inc("score", +100);

        animationBuilder()
                .duration(Duration.seconds(0.25))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(coin::removeFromWorld)
                .scale(coin)
                .from(new Point2D(1, 1))
                .to(new Point2D(0, 0))
                .buildAndPlay();

        var text = getUIFactory().newText("+100");
        text.setFont(getUIFactory().newFont(FontType.GAME, 26.0));
        text.setStroke(Color.RED);
        text.setStrokeWidth(2.75);

        var textEntity = entityBuilder()
                .at(coin.getPosition())
                .view(text)
                .buildAndAttach();

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .onFinished(textEntity::removeFromWorld)
                .translate(textEntity)
                .from(textEntity.getPosition())
                .to(textEntity.getPosition().subtract(0, 100))
                .buildAndPlay();
    }
}
