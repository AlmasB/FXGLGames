/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.gravity;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.gravity.scifi.ScifiType;
import com.almasb.fxglgames.gravity.scifi.UsableComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GravityFactory implements EntityFactory {

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLATFORM)
                .bbox(new HitBox("main", BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().density(0.03f));

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.ENEMY)
                .bbox(new HitBox("main", BoundingShape.box(40, 40)))
                .view(new Rectangle(40, 40, Color.RED))
                .with(physics)
                .build();
    }

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLATFORM)
                .viewWithBBox(new Rectangle(640 - 512, 64, Color.DARKCYAN))
                .zIndex(10000)
                .with(new PhysicsComponent())
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        Texture staticTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .subTexture(new Rectangle2D(0, 0, 32, 42));

        Texture animatedTexture = FXGL.getAssetLoader()
                .loadTexture("dude.png")
                .toAnimatedTexture(4, Duration.seconds(1));

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.PLAYER)
                .bbox(new HitBox("main", BoundingShape.circle(19)))
                .view(new Circle(19, 19, 19, Color.BLUE))
                .with(physics)
                //.with(new PlayerComponent(staticTexture, animatedTexture))
                .build();
    }

    @Spawns("button")
    public Entity newButton(SpawnData data) {
        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.BUTTON)
                .viewWithBBox(FXGL.getAssetLoader().loadTexture("push_button.png", 33, 22))
                .with(new UsableComponent(() -> FXGL.getGameWorld().spawn("block", 256, 352)))
                .build();
    }

    @Spawns("key")
    public Entity newKey(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.03f);
        physics.setFixtureDef(fd);

        return FXGL.entityBuilder()
                .at(data.getX(), data.getY())
                .type(ScifiType.KEY)
                .viewWithBBox(new Circle(10, 10, 10, Color.GOLD))
                .with(physics)
                .build();
    }
}
