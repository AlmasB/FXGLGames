package com.almasb.fxglgames.gravity.old;

import java.util.Iterator;

import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import com.almasb.fxgl.physics.box2d.dynamics.World;

public class App extends GameEnvironment {
    /**
     * Just a convenient references for player and world
     * the actual object is GameEnvironment.player/world
     */
    private World world;
    private Player player;
    private Viewport viewport = new Viewport();

    private Level level;

    @Override
    protected Parent createContent() {
        initLevel(0);

        Pane appRoot = new Pane();
        appRoot.getChildren().addAll(LEVEL_ROOT, UI_ROOT);
        return appRoot;
    }

    // TODO: design UI
    private void createUI() {
        ProgressBar hpBar = new ProgressBar();
        hpBar.setTranslateX(50);
        hpBar.setTranslateY(50);
        hpBar.setStyle("-fx-accent: rgb(0, 255, 0);");
        hpBar.progressProperty().bind(player.health.divide(10.0f));

        ProgressBar gBar = new ProgressBar();
        gBar.setTranslateX(50);
        gBar.setTranslateY(75);
        gBar.progressProperty().bind(player.power.divide(player.maxPower.multiply(1.0f)));

        Text score = new Text();
        score.setTranslateX(50);
        score.setTranslateY(25);
        score.setFont(Config.FONT);
        score.setFill(Color.GOLD);
        score.textProperty().bind(player.score.asString());

        UI_ROOT.getChildren().setAll(score, hpBar, gBar, createInfo());
    }

    private ChangeListener<? super Number> playerMoveListener = null;

    @Override
    protected void nextLevel() {
        timer.stop();

        level.gameObjects.clear();

        player.translateXProperty().removeListener(playerMoveListener);
        initLevel(1);
        timer.start();
    }

    private void initLevel(int levelNumber) {
        level = new Level(Config.Text.LEVEL_DATA.get(levelNumber));

        world = getWorld();
        player = getPlayer();

        LEVEL_ROOT.setLayoutX(0);
        viewport.setTranslateX(0);

        createUI();

        playerMoveListener = (obs, old, newValue) -> {
            int offset = newValue.intValue();

            if (offset > 640 && offset < level.getWidth() - 640) {
                LEVEL_ROOT.setLayoutX((-offset + 640) * Config.resolutionScale.get());
                viewport.setTranslateX(offset - 640);
            }
        };
        player.translateXProperty().addListener(playerMoveListener);

        LEVEL_OBJECTS.setAll(level.gameObjects);
        LEVEL_ROOT.getChildren().setAll(level.gameObjects);
    }

    Text debug = new Text();

    // TODO: remove
    private VBox createInfo() {
        VBox vbox = new VBox(10);
        vbox.setTranslateX(800);
        vbox.setTranslateY(200);

        vbox.getChildren().addAll(
                new Text("WASD - gravity defying movement"),
                new Text("G - alter gravity"),
                new Text("Q - Force 'pull'"),
                new Text("E - Force 'push"),
                debug);

        return vbox;
    }

    @Override
    protected void update() {
        if (keys[UP] && player.power.get() >= skillCosts[UP]) {
            player.power.set(player.power.get() - skillCosts[UP]);
            player.moveUp();
        }

        if (keys[DOWN]) {
        }

        if (keys[LEFT]) {
            player.moveLeft();
        }

        if (keys[RIGHT]) {
            player.moveRight();
        }

        if (keys[GRAVITY] && player.power.get() >= skillCosts[GRAVITY]) {
            for (GameObject obj : LEVEL_OBJECTS) {
                if (obj.isPhysicsSupported()) {
                    if (viewport.isColliding(obj)) {
                        if (obj.getType() != GameObject.Type.BULLET)
                            obj.body.setGravityScale(-1);
                        if (obj.getType() == GameObject.Type.ENEMY)
                            ((Enemy)obj).setUnstable();
                    }
                    else {
                        obj.body.setGravityScale(1);
                    }
                }
            }

            player.power.set(player.power.get() - skillCosts[GRAVITY]);
        }
        else {
            for (GameObject obj : LEVEL_OBJECTS) {
                if (obj.isPhysicsSupported()) {
                    if (obj.getType() != GameObject.Type.BULLET)
                        obj.body.setGravityScale(1);
                }
            }
        }

        world.step(Config.TIME_STEP, 8, 3);

        for (Iterator<GameObject> it = LEVEL_OBJECTS.iterator(); it.hasNext(); ) {
            GameObject obj = it.next();

            if (!obj.isPhysicsSupported() && player != obj) {
                // do our own fast collision check
                if (player.isColliding(obj)) {
                    player.onCollide(obj);
                    obj.onCollide(player);
                }
            }

            if (obj.getType() == GameObject.Type.BULLET && !viewport.isColliding(obj))
                obj.onDeath();

            obj.update();
            if (!obj.isAlive()) {
                it.remove();
                LEVEL_ROOT.getChildren().remove(obj);
            }
        }

        // add pending new objects
        // add to view list
        LEVEL_ROOT.getChildren().addAll(tmpList);

        // add to update list
        LEVEL_OBJECTS.addAll(tmpList);

        tmpList.clear();

        //debug.setText("Body count: " + world.getBodyCount());
    }

    @Override
    protected void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case SPACE:
                break;
        }

        if (keys[PULL] && player.power.get() >= skillCosts[PULL]) {
            player.power.set(player.power.get() - skillCosts[PULL]);
            for (GameObject obj : LEVEL_OBJECTS) {
                if (obj instanceof Enemy && viewport.isColliding(obj)) {
                    ((Enemy)obj).setUnstable();
                    obj.body.applyForceToCenter((obj.body.getPosition().sub(player.body.getPosition()).mul(-30)));
                }
            }
        }

        if (keys[PUSH] && player.power.get() >= skillCosts[PUSH]) {
            player.power.set(player.power.get() - skillCosts[PUSH]);
            for (GameObject obj : LEVEL_OBJECTS) {
                if (obj instanceof Enemy && viewport.isColliding(obj)) {
                    ((Enemy)obj).setUnstable();
                    obj.body.applyForceToCenter((obj.body.getPosition().sub(player.body.getPosition()).mul(30)));
                }
            }
        }
    }

    @Override
    protected void handleKeyReleased(KeyEvent event) {}
}
