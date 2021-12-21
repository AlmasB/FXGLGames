package com.almasb.fxglgames.td;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxglgames.td.collision.BulletEnemyHandler;
import com.almasb.fxglgames.td.event.EnemyKilledEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a tower defense game.
 *
 * Demo:
 * 1. Enemies move using waypoints
 * 2. Player can place towers
 * 3. Towers can shoot enemies
 * 4. Game ends if enemies are dead or have reached the last waypoint
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseApp extends GameApplication {

    // TODO: add HP components
    // TODO: assign bullet data from tower that shot it

    // TODO: read from level data
    private int levelEnemies = 10;

    private Point2D enemySpawnPoint = new Point2D(50, 0);

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Tower Defense");
        settings.setVersion("0.3");
        settings.setWidth(1280);
        settings.setHeight(768);
        settings.setIntroEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
//        Input input = getInput();
//
//        input.addAction(new UserAction("Place Tower") {
//            private Rectangle2D worldBounds = new Rectangle2D(0, 0, getAppWidth(), getAppHeight() - 100 - 40);
//
//            @Override
//            protected void onActionBegin() {
//                if (worldBounds.contains(input.getMousePositionWorld())) {
//                    placeTower();
//                }
//            }
//        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("numEnemies", levelEnemies);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TowerDefenseFactory());

        setLevelFromMap("td1.tmx");

        // TODO: optimise and remove cells
//        for (int y = 0; y < 720 / 64 + 1; y++) {
//            for (int x = 0; x < 1280 / 64 + 1; x++) {
//                var cell = spawn("Cell", x * 64, y * 64);
//            }
//        }

//        waypoints.addAll(Arrays.asList(
//                new Point2D(700, 0),
//                new Point2D(700, 300),
//                new Point2D(50, 300),
//                new Point2D(50, 450),
//                new Point2D(700, 500)
//        ));


        BooleanProperty enemiesLeft = new SimpleBooleanProperty();
        enemiesLeft.bind(getip("numEnemies").greaterThan(0));

        getGameTimer().runAtIntervalWhile(this::spawnEnemy, Duration.seconds(1), enemiesLeft);
//
//        getEventBus().addEventHandler(EnemyKilledEvent.ANY, this::onEnemyKilled);
//        getEventBus().addEventHandler(EnemyReachedGoalEvent.ANY, e -> gameOver());
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
    }

    public void onCellClicked(Entity cell) {
        var tower = spawn("Tower", cell.getPosition());
    }

    private TowerData selectedTower;

    @Override
    protected void initUI() {
        var data = new TowerData();
        data.setImageName("tower.png");

        addUINode(new TowerIcon(data), 1000, 680);


//        Rectangle uiBG = new Rectangle(getAppWidth(), 100);
//        uiBG.setTranslateY(500);
//
//        getGameScene().addUINode(uiBG);
//
//        for (int i = 0; i < 4; i++) {
//            int index = i + 1;
//
//            Color color = FXGLMath.randomColor();
//            TowerIcon icon = new TowerIcon(color);
//            icon.setTranslateX(10 + i * 100);
//            icon.setTranslateY(500);
//            icon.setOnMouseClicked(e -> {
//                selectedColor = color;
//                selectedIndex = index;
//            });
//
//            getGameScene().addUINode(icon);
//        }
    }

    private void spawnEnemy() {
        inc("numEnemies", -1);

        var wayEntity = getGameWorld().getSingleton(EntityType.WAY);
        Polygon p = wayEntity.getObject("polygon");

        spawn("Enemy", new SpawnData().put("way", Way.fromPolygon(p, wayEntity.getX(), wayEntity.getY())));
    }

//    private void placeTower() {
//        spawn("Tower",
//                new SpawnData(getInput().getMouseXWorld(), getInput().getMouseYWorld())
//                        .put("color", selectedColor)
//                        .put("index", selectedIndex)
//        );
//    }

    private void onEnemyKilled(EnemyKilledEvent event) {
        levelEnemies--;

        if (levelEnemies == 0) {
            gameOver();
        }

        Entity enemy = event.getEnemy();
        Point2D position = enemy.getPosition();

        Text xMark = getUIFactoryService().newText("X", Color.RED, 24);
        xMark.setTranslateX(position.getX());
        xMark.setTranslateY(position.getY() + 20);

        getGameScene().addGameView(new GameView(xMark, 1000));
    }

    private void gameOver() {
        showMessage("Demo Over. Thanks for playing!", getGameController()::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
