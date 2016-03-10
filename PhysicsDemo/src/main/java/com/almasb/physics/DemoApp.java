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

package com.almasb.physics;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsParticleComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.BoundingBox;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleType;

import java.util.EnumSet;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DemoApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Remove Door", KeyCode.F));
    }

    @Override
    protected void initAssets() {

    }

    private Entity trapDoor;

    @Override
    protected void initGame() {
        spawnWall(0, 500, 800, 100);
        spawnWall(500, 475, 50, 25);
        spawnWall(650, 475, 50, 25);

        spawnWall(540, 0, 10, 290);
        spawnWall(590, 0, 10, 290);
        spawnWall(540, 290, 40, 5);
        trapDoor = spawnWall(580, 290, 10, 5);

        ParticleGroupDef def = new ParticleGroupDef();
        def.setTypes(EnumSet.of(ParticleType.WATER));

        PhysicsParticleComponent physics = new PhysicsParticleComponent();
        physics.setColor(Color.DARKBLUE);
        physics.setDefinition(def);

        GameEntity water = new GameEntity();
        water.getPositionComponent().setValue(550, -200);
        water.getBoundingBoxComponent().addHitBox(new HitBox("TEST", new BoundingBox(0, 0, 40, 250)));
        water.addComponent(physics);

        GameEntity box = new GameEntity();
        box.getPositionComponent().setValue(600, 450);
        box.getMainViewComponent().setView(new Rectangle(40, 40, Color.BLUE), true);

        PhysicsComponent component = new PhysicsComponent();
        component.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.01f);

        component.setFixtureDef(fd);

        box.addComponent(component);

        getGameWorld().addEntities(box, water);
    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {

    }

    private Entity spawnWall(double x, double y, double w, double h) {
        GameEntity entity = new GameEntity();
        entity.getPositionComponent().setValue(x, y);
        entity.getMainViewComponent().setView(new Rectangle(w, h), true);
        entity.addComponent(new PhysicsComponent());

        getGameWorld().addEntity(entity);

        return entity;
    }

    @OnUserAction(name = "Remove Door", type = ActionType.ON_ACTION_BEGIN)
    public void removeDoor() {
        if (trapDoor == null) {
            trapDoor = spawnWall(580, 290, 10, 5);
        } else {
            trapDoor.removeFromWorld();
            trapDoor = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
