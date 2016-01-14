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

package com.almasb.pong;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("0.1dev");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Up", KeyCode.W));
        input.addInputMapping(new InputMapping("Down", KeyCode.S));
    }

    @Override
    protected void initAssets() {

    }

    private IntegerProperty scorePlayer, scoreEnemy;

    @Override
    protected void initGame() {
        scorePlayer = new SimpleIntegerProperty(0);
        scoreEnemy = new SimpleIntegerProperty(0);

        initBackground();
        initScreenBounds();
        initBall();
        initPlayerBat();
        initEnemyBat();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.Type.BALL,
                EntityTypes.Type.LEFT_WALL) {
            @Override
            protected void onCollisionBegin(Entity ball, Entity wall) {
                scoreEnemy.set(scoreEnemy.get() + 1);
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityTypes.Type.BALL,
                EntityTypes.Type.RIGHT_WALL) {
            @Override
            protected void onCollisionBegin(Entity ball, Entity wall) {
                scorePlayer.set(scorePlayer.get() + 1);
            }
        });
    }

    @Override
    protected void initUI() {
        AppController controller = new AppController();
        Parent ui = getAssetLoader().loadFXML("main.fxml", controller);

        controller.getLabelScorePlayer().textProperty().bind(scorePlayer.asString());
        controller.getLabelScoreEnemy().textProperty().bind(scoreEnemy.asString());

        getGameScene().addUINode(ui);
    }

    @Override
    protected void onUpdate() {}

    private void initBackground() {
        Entity bg = Entity.noType();
        bg.setSceneView(new Rectangle(getWidth(), getHeight(), Color.rgb(0, 0, 5)));

        getGameWorld().addEntity(bg);
    }

    private void initScreenBounds() {
        PhysicsEntity top = new PhysicsEntity(EntityTypes.Type.WALL);
        top.setPosition(0, 0 - 100);
        top.setSceneView(new Rectangle(getWidth(), 100));

        PhysicsEntity bot = new PhysicsEntity(EntityTypes.Type.WALL);
        bot.setPosition(0, getHeight());
        bot.setSceneView(new Rectangle(getWidth(), 100));

        PhysicsEntity left = new PhysicsEntity(EntityTypes.Type.LEFT_WALL);
        left.setPosition(0 - 100, 0);
        left.setSceneView(new Rectangle(100, getHeight()));
        left.setCollidable(true);

        PhysicsEntity right = new PhysicsEntity(EntityTypes.Type.RIGHT_WALL);
        right.setPosition(getWidth(), 0);
        right.setSceneView(new Rectangle(100, getHeight()));
        right.setCollidable(true);

        getGameWorld().addEntities(top, bot, left, right);
    }

    private void initBall() {
        PhysicsEntity ball = new PhysicsEntity(EntityTypes.Type.BALL);
        ball.setBodyType(BodyType.DYNAMIC);
        ball.setSceneView(new Circle(5, Color.LIGHTGRAY));
        ball.setPosition(getWidth() / 2 - 5, getHeight() / 2 - 5);
        ball.addControl(new BallControl());
        ball.setCollidable(true);

        FixtureDef def = new FixtureDef();
        def.setDensity(0.3f);
        def.setRestitution(1.0f);

        ball.setFixtureDef(def);
        ball.setOnPhysicsInitialized(() -> ball.setLinearVelocity(-5, -5));

        getGameWorld().addEntity(ball);
    }

    private BatControl playerBat;

    private void initPlayerBat() {
        PhysicsEntity bat = new PhysicsEntity(EntityTypes.Type.PLAYER_BAT);
        bat.setBodyType(BodyType.KINEMATIC);
        bat.setPosition(getWidth() / 4, getHeight() / 2 - 30);
        bat.setSceneView(new Rectangle(20, 60, Color.LIGHTGRAY));

        playerBat = new BatControl();
        bat.addControl(playerBat);

        getGameWorld().addEntity(bat);
    }

    private void initEnemyBat() {
        PhysicsEntity bat = new PhysicsEntity(EntityTypes.Type.PLAYER_BAT);
        bat.setBodyType(BodyType.KINEMATIC);
        bat.setPosition(3 * getWidth() / 4 - 20, getHeight() / 2 - 30);
        bat.setSceneView(new Rectangle(20, 60, Color.LIGHTGRAY));
        bat.addControl(new EnemyBatControl());

        getGameWorld().addEntity(bat);
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION)
    public void up() {
        playerBat.up();
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION)
    public void down() {
        playerBat.down();
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION_END)
    public void stopBat() {
        playerBat.stop();
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION_END)
    public void stopBat2() {
        playerBat.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
