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
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
// NOTE: this import above is crucial, it pulls in many useful methods

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
 * Although the code is self-explanatory, some may find the comments useful
 * for following the code.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DropApp extends GameApplication {

    /**
     * Types of entities in this game.
     */
    public enum Type {
        DROPLET, BUCKET
    }

    @Override
    protected void initSettings(GameSettings settings) {
        // initialize common game / window settings.
        settings.setTitle("Drop");
        settings.setVersion("1.0");
        settings.setWidth(480);
        settings.setHeight(800);
    }

    @Override
    protected void initGame() {
        spawnBucket();

        // creates a timer that runs spawnDroplet() every second
        run(() -> spawnDroplet(), Duration.seconds(1));

        // loop background music located in /resources/assets/music/
        loopBGM("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(Type.BUCKET, Type.DROPLET, (bucket, droplet) -> {

            // code in this block is called when there is a collision between Type.BUCKET and Type.DROPLET

            // remove the collided droplet from the game
            droplet.removeFromWorld();

            // play a sound effect located in /resources/assets/sounds/
            play("drop.wav");
        });
    }

    @Override
    protected void onUpdate(double tpf) {

        // for each entity of Type.DROPLET translate (move) it down
        getGameWorld().getEntitiesByType(Type.DROPLET).forEach(droplet -> droplet.translateY(150 * tpf));
    }

    private void spawnBucket() {
        // build an entity with Type.BUCKET
        // at the position X = getAppWidth() / 2 and Y = getAppHeight() - 200
        // with a view "bucket.png", which is an image located in /resources/assets/textures/
        // also create a bounding box from that view
        // make the entity collidable
        // finally, complete building and attach to the game world

        Entity bucket = entityBuilder()
                .type(Type.BUCKET)
                .at(getAppWidth() / 2, getAppHeight() - 200)
                .viewWithBBox("bucket.png")
                .collidable()
                .buildAndAttach();

        // bind bucket's X value to mouse X
        bucket.xProperty().bind(getInput().mouseXWorldProperty());
    }

    private void spawnDroplet() {
        entityBuilder()
                .type(Type.DROPLET)
                .at(FXGLMath.random(0, getAppWidth() - 64), 0)
                .viewWithBBox("droplet.png")
                .collidable()
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
