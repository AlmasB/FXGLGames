/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.drop;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

// NOTE: this is crucial, the import pulls in many useful methods
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an FXGL version of the libGDX simple game tutorial, which can be found
 * here - https://github.com/libgdx/libgdx/wiki/A-simple-game
 *
 * The player can move the bucket left and right to catch water droplets.
 * There are no win/lose conditions.
 *
 * Note: for simplicity's sake all of the code is kept in this file.
 * In addition, most of typical FXGL API is not used to avoid overwhelming
 * FXGL beginners with a lot of new concepts to learn.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DropApp extends GameApplication {

    /**
     * Types of entities in this game.
     */
    public enum DropType {
        DROPLET, BUCKET
    }

    private Entity bucket;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Drop");
        settings.setVersion("1.0");
        settings.setWidth(480);
        settings.setHeight(800);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.A, "Move Left", () -> bucket.translateX(-300 * tpf()));
        onKey(KeyCode.D, "Move Right", () -> bucket.translateX(300 * tpf()));
    }

    @Override
    protected void initGame() {
        bucket = spawnBucket();

        run(() -> spawnDroplet(), Duration.seconds(1));

        loopBGM("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(DropType.BUCKET, DropType.DROPLET, (bucket, droplet) -> {
            droplet.removeFromWorld();

            play("drop.wav");
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        getGameWorld().getEntitiesByType(DropType.DROPLET).forEach(droplet -> droplet.translateY(150 * tpf));
    }

    private Entity spawnBucket() {
        return entityBuilder()
                .type(DropType.BUCKET)
                .at(getAppWidth() / 2, getAppHeight() - 200)
                .viewWithBBox("bucket.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    private Entity spawnDroplet() {
        return entityBuilder()
                .type(DropType.DROPLET)
                .at(FXGLMath.random(getAppWidth() - 64), 0)
                .viewWithBBox("droplet.png")
                .with(new CollidableComponent(true))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
