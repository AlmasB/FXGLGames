/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.mario;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.SpawnSymbol;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.TextEntityFactory;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxglgames.mario.control.PhysicsCarryControl;
import com.almasb.fxglgames.mario.control.PhysicsLiftControl;
import com.almasb.fxglgames.mario.control.PlayerControl;
import com.almasb.fxglgames.mario.type.EntityType;
import com.almasb.fxglgames.mario.type.PickupType;
import com.almasb.fxglgames.mario.type.PlatformType;
import com.almasb.fxglgames.mario.type.SubTypeComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class MarioFactory implements TextEntityFactory {

    private GameEntity makeGenericPlatform(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EntityType.PLATFORM)
                .with(new SubTypeComponent(PlatformType.NORMAL))
                .build();
    }

    @SpawnSymbol('c')
    public GameEntity makeCheckpoint(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EntityType.CHECKPOINT)
                .viewFromTextureWithBBox("checkpoint.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @SpawnSymbol('f')
    public GameEntity makeFinish(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EntityType.FINISH)
                .viewFromTextureWithBBox("finish.png")
                .with(new CollidableComponent(true))
                .build();
    }

    @SpawnSymbol('g')
    public GameEntity makePickupCoin(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(EntityType.PICKUP)
                .viewFromTextureWithBBox("coin.png")
                .with(new CollidableComponent(true))
                .with(new SubTypeComponent(PickupType.COIN))
                .build();
    }

    @SpawnSymbol('b')
    public GameEntity makePlatformBegin(SpawnData data) {
        GameEntity e = makeGenericPlatform(data);
        e.getViewComponent().setTexture("platform_begin.png", true);

        PhysicsComponent physics = new PhysicsComponent();
        e.addComponent(physics);

        return e;
    }

    @SpawnSymbol('e')
    public GameEntity makePlatformEnd(SpawnData data) {
        GameEntity e = makeGenericPlatform(data);
        e.getViewComponent().setTexture("platform_end.png", true);

        PhysicsComponent physics = new PhysicsComponent();
        e.addComponent(physics);

        return e;
    }

    @SpawnSymbol('p')
    public GameEntity makePlatform(SpawnData data) {
        GameEntity e = makeGenericPlatform(data);
        e.getViewComponent().setTexture("platform.png", true);

        PhysicsComponent physics = new PhysicsComponent();
        e.addComponent(physics);

        return e;
    }

    @SpawnSymbol('i')
    public GameEntity makePlatformInvisible(SpawnData data) {
        GameEntity e = Entities.builder()
                .from(data)
                .type(EntityType.PLATFORM)
                .viewFromTextureWithBBox("platform.png")
                .with(new CollidableComponent(true))
                .with(new SubTypeComponent(PlatformType.INVISIBLE))
                .build();

        e.getViewComponent().getView().setVisible(false);
        return e;
    }

    @SpawnSymbol('1')
    public GameEntity makePlatformLift(SpawnData data) {
        GameEntity e = makePlatform(data);
        Entities.getPhysics(e).setBodyType(BodyType.KINEMATIC);
        e.addControl(new PhysicsLiftControl(data.getY()));

        return e;
    }

    @SpawnSymbol('2')
    public GameEntity makePlatformCarry(SpawnData data) {
        GameEntity e = makePlatform(data);
        Entities.getPhysics(e).setBodyType(BodyType.KINEMATIC);
        e.addControl(new PhysicsCarryControl(data.getX()));

        return e;
    }

    @SpawnSymbol('0')
    public GameEntity makeBlock(SpawnData data) {
        GameEntity e = makeGenericPlatform(data);
        e.getViewComponent().setTexture("block.png", true);

        PhysicsComponent physics = new PhysicsComponent();
        e.addComponent(physics);

        return e;
    }


//        parser.addEntityProducer('3', this::makePlatformLiftCarry);
//
//
//        parser.addEntityProducer('A', this::makeEnemySnake);
//        parser.addEntityProducer('B', this::makeEnemyPlant);

//    private Entity makeEnemySnake(double x, double y) {
//        Entity snake = makeGenericEnemy(x, y);
//        snake.setProperty(Property.SUB_TYPE, EnemyType.SNAKE);
//        snake.setGraphics(assets.getTexture("snake2.png").toStaticAnimatedTexture(6, Duration.seconds(3)));
//        snake.addControl(new PatrolControl(x * MarioApp.BLOCK_SIZE));
//        //snake.addControl(new AIProximityControl(player));
//        return snake;
//    }
//
//    private Entity makeEnemyPlant(double x, double y) {
//        Entity plant = makeGenericEnemy(x, y);
//        plant.setProperty(Property.SUB_TYPE, EnemyType.PLANT);
//        plant.setGraphics(assets.getTexture("enemy_plant.png").toStaticAnimatedTexture(4, Duration.seconds(3)));
//        //plant.addControl(new AIProximityControl(player));
//        plant.addControl(new EnemyPlantControl(player));
//        return plant;
//    }



//
//    private Entity makePlatformLift(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform.png"));
//        platform.addControl(new PhysicsLiftControl(y * MarioApp.BLOCK_SIZE));
//        return platform;
//    }
//
//    private Entity makePlatformCarry(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform.png"));
//        platform.addControl(new PatrolControl(x * MarioApp.BLOCK_SIZE));
//        return platform;
//    }
//
//    private Entity makePlatformLiftCarry(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform.png"));
//        platform.addControl(new PhysicsLiftControl(y * MarioApp.BLOCK_SIZE));
//        platform.addControl(new PatrolControl(x * MarioApp.BLOCK_SIZE));
//        return platform;
//    }

    @SpawnSymbol('s')
    public GameEntity makePlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        // TODO: something wrong with physics body config
        // it's possible to get stuck in platforms causing endless loop?
        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.3f);

        physics.setFixtureDef(fd);

        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);

        physics.setBodyDef(bd);
        physics.setBodyType(BodyType.DYNAMIC);

        GameEntity p = Entities.builder()
                .at(data.getX(), data.getY() - 96)
                .type(EntityType.PLAYER)
                //.viewFromTexture("player.png")
                .with(new CollidableComponent(true))
                .with(physics)
                .with(new SControl())
                //.with(new PlayerControl())
                .build();

        p.getBoundingBoxComponent().addHitBox(new HitBox("BODY", BoundingShape.box(77, 96)));

        return p;
    }

    @Override
    public char emptyChar() {
        return ' ';
    }

    @Override
    public int blockWidth() {
        return 32;
    }

    @Override
    public int blockHeight() {
        return 32;
    }
}
