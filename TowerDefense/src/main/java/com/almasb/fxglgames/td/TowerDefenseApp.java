package com.almasb.fxglgames.td;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxglgames.td.components.EnemyComponent;
import com.almasb.fxglgames.td.data.*;
import com.almasb.fxglgames.td.ui.*;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.td.data.Config.*;
import static com.almasb.fxglgames.td.data.Vars.*;

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

    private List<TowerData> towerData;
    private List<LevelData> levelData;

    private LevelData currentLevel;

    private TowerSelectionBox towerSelectionBox;
    private WaveIcon waveIcon;

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
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(NUM_ENEMIES, 0);
        vars.put(MONEY, STARTING_MONEY);
        vars.put(PLAYER_HP, STARTING_HP);
        vars.put(CURRENT_WAVE, 0);
    }

    @Override
    protected void initGame() {
        initVarListeners();

        loadTowerData();
        loadLevelData();

        getGameWorld().addEntityFactory(new TowerDefenseFactory());

        // construct UI objects
        towerSelectionBox = new TowerSelectionBox(towerData);
        waveIcon = new WaveIcon();

        nextLevel();
    }

    private void initVarListeners() {
        getWorldProperties().<Integer>addListener(NUM_ENEMIES, (old, newValue) -> {
            if (newValue == 0) {
                nextWave();
            }
        });

        getWorldProperties().<Integer>addListener(PLAYER_HP, (old, newValue) -> {
            if (newValue == 0) {
                gameOver();
            }
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

    private void loadLevelData() {
        List<String> levelNames = List.of(
                "level1.json"
        );

        levelData = levelNames.stream()
                .map(name -> getAssetLoader().loadJSON("levels/" + name, LevelData.class).get())
                .collect(Collectors.toList());
    }

    private void nextLevel() {
        if (levelData.isEmpty()) {
            gameOver();
            return;
        }

        currentLevel = levelData.remove(0);
        set(CURRENT_WAVE, 0);

        waveIcon.setMaxWave(currentLevel.maxWaveIndex());

        setLevelFromMap("tmx/" + currentLevel.map());

        getGameWorld().getEntitiesFiltered(e -> e.isType("TiledMapLayer"))
                .forEach(e -> {
                    e.getViewComponent().addOnClickHandler(event -> {
                        towerSelectionBox.setVisible(false);
                    });
                });

        nextWave();
    }

    private void nextWave() {
        if (geti(CURRENT_WAVE) < currentLevel.maxWaveIndex()) {
            inc(CURRENT_WAVE, 1);

            currentLevel.waves()
                    .stream()
                    .filter(w -> w.index() == geti(CURRENT_WAVE))
                    .forEach(wave -> {
                        spawnWave(wave);

                        inc(NUM_ENEMIES, wave.amount());
                    });
        } else {
            nextLevel();
        }
    }

    private void spawnWave(WaveData wave) {
        for (int i = 0; i < wave.amount(); i++) {
            runOnce(() -> {

                var wayEntity = getGameWorld().getSingleton(e ->
                        e.isType(EntityType.WAY) && e.getString("name").equals(wave.way())
                );

                EnemyData data = getAssetLoader().loadJSON("enemies/" + wave.enemy(), EnemyData.class).get();

                Polygon p = wayEntity.getObject("polygon");

                spawnWithScale(
                        "Enemy",
                        new SpawnData()
                                .put("way", Way.fromPolygon(p, wayEntity.getX(), wayEntity.getY()))
                                .put("enemyData", data),
                        Duration.seconds(0.45),
                        Interpolators.ELASTIC.EASE_OUT()
                );

            }, Duration.seconds(i));
        }
    }

    @Override
    protected void initUI() {
        towerSelectionBox.setVisible(false);

        addUINode(towerSelectionBox);

        addUINode(new MoneyIcon(), 10, 10);
        addUINode(new HPIcon(), 10, 90);

        addUINode(waveIcon, 10, 250);

        addUINode(new EnemiesIcon(), 10, 170);
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

    public void onEnemyKilled(Entity enemy) {
        inc(NUM_ENEMIES, -1);
        
        inc(MONEY, enemy.getComponent(EnemyComponent.class).getData().reward());
    }

    public void onEnemyReachedEnd(Entity enemy) {
        inc(NUM_ENEMIES, -1);

        inc(PLAYER_HP, -1);
    }

    private void gameOver() {
        showMessage("Demo Over. Thanks for playing!", getGameController()::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
