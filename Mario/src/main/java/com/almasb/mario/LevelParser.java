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

package com.almasb.mario;

import com.almasb.mario.EntityType.EnemyType;
import com.almasb.mario.EntityType.PickupType;
import com.almasb.mario.EntityType.PlatformType;
import com.almasb.mario.EntityType.Property;
import com.almasb.mario.EntityType.Type;

public class LevelParser {

//    private Entity player = new Entity(Type.PLAYER);
//    private Level level;
//
//    private Assets assets;
//
//    public LevelParser(Assets assets) {
//        this.assets = assets;
//
//        TextLevelParser parser = new TextLevelParser();
//        parser.addEntityProducer('b', this::makePlatformBegin);
//        parser.addEntityProducer('e', this::makePlatformEnd);
//        parser.addEntityProducer('p', this::makePlatform);
//        parser.addEntityProducer('i', this::makePlatformInvisible);
//        parser.addEntityProducer('0', this::makeBlock);
//        parser.addEntityProducer('1', this::makePlatformLift);
//        parser.addEntityProducer('2', this::makePlatformCarry);
//        parser.addEntityProducer('3', this::makePlatformLiftCarry);
//
//        parser.addEntityProducer('c', this::makeCheckpoint);
//        parser.addEntityProducer('s', this::makePlayer);
//        parser.addEntityProducer('f', this::makeFinish);
//
//        parser.addEntityProducer('A', this::makeEnemySnake);
//        parser.addEntityProducer('B', this::makeEnemyPlant);
//
//        parser.addEntityProducer('z', this::makePickupGhostBomb);
//        parser.addEntityProducer('g', this::makePickupCoin);
//
//        try {
//            level = parser.parse("levels/0.txt");
//        }
//        catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public Level getLevel() {
//        return level;
//    }
//
//    public Entity getPlayer() {
//        return player;
//    }
//
//    private Entity makeGenericEnemy(double x, double y) {
//        Entity enemy = new Entity(Type.ENEMY);
//        enemy.setPosition(x * MarioApp.BLOCK_SIZE, y * MarioApp.BLOCK_SIZE);
//        enemy.setCollidable(true);
//        return enemy;
//    }
//
//    private Entity makeGenericPickup(double x, double y) {
//        Entity pickup = new Entity(Type.PICKUP);
//        pickup.setPosition(x * MarioApp.BLOCK_SIZE, y * MarioApp.BLOCK_SIZE);
//        pickup.setCollidable(true);
//        return pickup;
//    }
//
//    private Entity makeGenericPlatform(double x, double y) {
//        Entity platform = new Entity(Type.PLATFORM);
//        platform.setPosition(x * MarioApp.BLOCK_SIZE, y * MarioApp.BLOCK_SIZE);
//        return platform;
//    }
//
//    private Entity makePickupCoin(double x, double y) {
//        Entity coin = makeGenericPickup(x, y);
//        coin.setProperty(Property.SUB_TYPE, PickupType.COIN);
//        coin.setGraphics(assets.getTexture("coin.png"));
//        return coin;
//    }
//
//    private Entity makePickupGhostBomb(double x, double y) {
//        Entity coin = makeGenericPickup(x, y);
//        coin.setProperty(Property.SUB_TYPE, PickupType.GHOST_BOMB);
//        coin.setGraphics(assets.getTexture("ghost_bom.png"));
//        return coin;
//    }
//
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
//    private Entity makePlayer(double x, double y) {
//        player.setPosition(x*32, y * 32);
//        player.setGraphics(assets.getTexture("player.png"));
//        player.setCollidable(true);
//        return player;
//    }
//
//    private Entity makeCheckpoint(double x, double y) {
//        Entity checkpoint = new Entity(Type.CHECKPOINT);
//        checkpoint.setPosition(x*32, y*32);
//        checkpoint.setGraphics(assets.getTexture("checkpoint.png"));
//        checkpoint.setCollidable(true);
//
//        return checkpoint;
//    }
//
//    private Entity makeFinish(double x, double y) {
//        Entity finish = new Entity(Type.FINISH);
//        finish.setPosition(x*32, y*32);
//        finish.setGraphics(assets.getTexture("finish.png"));
//        finish.setCollidable(true);
//
//        return finish;
//    }
//
//    private Entity makePlatformBegin(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform_begin.png"));
//
//        return platform;
//    }
//
//    private Entity makePlatformEnd(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform_end.png"));
//
//        return platform;
//    }
//
//    private Entity makePlatform(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform.png"));
//
//        return platform;
//    }
//
//    private Entity makePlatformInvisible(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.INVISIBLE);
//        platform.setGraphics(assets.getTexture("platform.png"));
//        platform.setCollidable(true);
//        platform.setVisible(false);
//
//        return platform;
//    }
//
//    private Entity makePlatformLift(double x, double y) {
//        Entity platform = makeGenericPlatform(x, y);
//        platform.setProperty(Property.SUB_TYPE, PlatformType.NORMAL);
//        platform.setGraphics(assets.getTexture("platform.png"));
//        platform.addControl(new LiftControl(y * MarioApp.BLOCK_SIZE));
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
//        platform.addControl(new LiftControl(y * MarioApp.BLOCK_SIZE));
//        platform.addControl(new PatrolControl(x * MarioApp.BLOCK_SIZE));
//        return platform;
//    }
//
//    private Entity makeBlock(double x, double y) {
//        Entity block = new Entity(Type.PLATFORM);
//        block.setPosition(x*32, y*32);
//        block.setGraphics(assets.getTexture("block.png"));
//
//        return block;
//    }
}
