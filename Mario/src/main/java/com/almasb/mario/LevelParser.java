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


public class LevelParser {


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

}
