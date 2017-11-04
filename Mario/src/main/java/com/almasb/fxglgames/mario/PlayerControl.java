/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.mario;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.FXGL;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends Control {

    private PositionComponent position;
    private ViewComponent view;
    private PhysicsComponent physics;

    private LocalTimer jumpTimer;

    private AnimatedTexture animatedTexture;

    private AnimationChannel animStand, animWalk, animJump, animFall;

    public PlayerControl() {
//        animWalk = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 0, 3);
//        animStand = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(1), 1, 1);
//        animJump = new AnimationChannel("newdude.png", 4, 32, 42, Duration.seconds(0.75), 1, 1);

        animWalk = new AnimationChannel("robot.png", 7, 275, 275, Duration.seconds(1), 0, 15);
        animStand = new AnimationChannel("robot_stand.png", 7, 275, 275, Duration.seconds(1.5), 0, 29);
        animJump = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.95), 0, 25);

//        animWalk = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1), 24, 35);
//        animStand = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(1.5), 0, 11);
//        animJump = new AnimationChannel("animation.png", 12, 77, 96, Duration.seconds(0.95), 48, 59);

        animFall = new AnimationChannel("robot_jump.png", 7, 275, 275, Duration.seconds(0.95), 25, 25);

        animatedTexture = new AnimatedTexture(animWalk);

        //animatedTexture.setAnimationChannel(animStand);
        animatedTexture.start(FXGL.getApp().getStateMachine().getPlayState());

        jumpTimer = FXGL.newLocalTimer();

        //java.awt.image.BufferedImage image = SwingFXUtils.fromFXImage(animatedTexture.getImage(), null);
//        try {
//            ImageIO.write(image, "png", new File("newdude.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onAdded(Entity entity) {
        view.getView().addNode(animatedTexture);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        boolean onGround = canJump;

        if (onGround) {
            if (Math.abs(physics.getVelocityX()) == 0) {
                animatedTexture.setAnimationChannel(animStand);
            } else {
                animatedTexture.setAnimationChannel(animWalk);
            }
        } else {
            if ((physics.getVelocityY()) < 0) {
                animatedTexture.playAnimationChannel(animJump);
            } else {
                // fall
                animatedTexture.setAnimationChannel(animFall);
            }
        }




        if (Math.abs(physics.getVelocityX()) < 140)
            physics.setVelocityX(0);
    }

    boolean canJump = false;

    public void left() {
        view.getView().setScaleX(-1);
        physics.setVelocityX(-150);
    }

    public void right() {
        view.getView().setScaleX(1);
        physics.setVelocityX(150);
    }

    public void jump() {
        if (jumpTimer.elapsed(Duration.seconds(0.25))) {
            if (canJump) {
                physics.setVelocityY(-250);
                canJump = false;
                jumpTimer.capture();
            }
        }
    }

    public void stop() {
        physics.setVelocityX(physics.getVelocityX() * 0.7);
    }

    public void shoot(Point2D endPoint) {
        double x = position.getX();
        double y = position.getY();

        Point2D velocity = endPoint
                .subtract(x, y)
                .normalize()
                .multiply(500);

        getEntity().getWorld().spawn("Arrow",
                new SpawnData(x, y)
                        .put("velocity", velocity)
                        .put("shooter", getEntity()));
    }
}
