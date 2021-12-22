package com.almasb.fxglgames.td;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxglgames.td.collision.BulletEnemyHandler;
import com.almasb.fxglgames.td.event.EnemyKilledEvent;
import com.almasb.fxglgames.td.ui.MoneyIcon;
import com.almasb.fxglgames.td.ui.TowerIcon;
import com.almasb.fxglgames.td.ui.TowerSelectionBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // TODO: read from level data
    private int levelEnemies = 10;

    private List<TowerData> towerData;

    private TowerSelectionBox towerSelectionBox;

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
//        onBtnDown(MouseButton.PRIMARY, () -> {
//            var x = getInput().getMouseXWorld();
//            var y = getInput().getMouseYWorld();
//
//            var isThereTowerBase = getGameWorld().getEntitiesByType(EntityType.TOWER_BASE)
//                    .stream()
//                    .anyMatch(e -> e.getBoundingBoxComponent().range(0, 0).contains(x, y));
//
//            if (!isThereTowerBase) {
//                towerSelectionBox.setVisible(false);
//            }
//        });

        onKey(KeyCode.F,() -> {
            inc("money", -50);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("numEnemies", levelEnemies);
        vars.put("money", 1000);
        vars.put("playerHP", 10);
    }

    @Override
    protected void initGame() {
        loadTowerData();

        getGameWorld().addEntityFactory(new TowerDefenseFactory());

        setLevelFromMap("td1.tmx");

        BooleanProperty enemiesLeft = new SimpleBooleanProperty();
        enemiesLeft.bind(getip("numEnemies").greaterThan(0));

        getGameTimer().runAtIntervalWhile(this::spawnEnemy, Duration.seconds(1), enemiesLeft);

//        getEventBus().addEventHandler(EnemyKilledEvent.ANY, this::onEnemyKilled);
//        getEventBus().addEventHandler(EnemyReachedGoalEvent.ANY, e -> gameOver());

        getGameWorld().getEntitiesFiltered(e -> e.isType("TiledMapLayer"))
                .forEach(e -> {
                    e.getViewComponent().addOnClickHandler(event -> {
                        towerSelectionBox.setVisible(false);
                    });
                });
    }

    private void loadTowerData() {
        List<String> towerNames = List.of(
                "tower1.json",
                "tower2.json",
                "tower3.json",
                "tower4.json",
                "tower5.json",
                "tower6.json"
        );

        towerData = towerNames.stream()
                .map(name -> getAssetLoader().loadJSON("towers/" + name, TowerData.class).get())
                .collect(Collectors.toList());
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new BulletEnemyHandler());
    }

    public void onCellClicked(Entity cell) {
        towerSelectionBox.setCell(cell);
        towerSelectionBox.setVisible(true);

        var x = cell.getX() > getAppWidth() / 2.0 ? cell.getX() - 250 : cell.getX();

        towerSelectionBox.setTranslateX(x);
        towerSelectionBox.setTranslateY(cell.getY());
    }

    public void onTowerSelected(Entity cell, TowerData data) {
        towerSelectionBox.setVisible(false);

        inc("money", -data.cost());

        var tower = spawn("Tower", new SpawnData(cell.getPosition()).put("towerData", data));
    }

    @Override
    protected void initUI() {
        towerSelectionBox = new TowerSelectionBox(towerData);
        towerSelectionBox.setVisible(false);

        addUINode(towerSelectionBox);

        var moneyIcon = new MoneyIcon();

        addUINode(moneyIcon, 10, 10);
    }

    private void spawnEnemy() {
        inc("numEnemies", -1);

        var wayEntity = getGameWorld().getSingleton(EntityType.WAY);
        Polygon p = wayEntity.getObject("polygon");

        spawnWithScale(
                "Enemy",
                new SpawnData().put("way", Way.fromPolygon(p, wayEntity.getX(), wayEntity.getY())),
                Duration.seconds(0.45),
                Interpolators.ELASTIC.EASE_OUT()
        );
    }

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
