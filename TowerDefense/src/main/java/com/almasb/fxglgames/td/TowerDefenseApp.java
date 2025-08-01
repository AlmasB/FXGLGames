package com.almasb.fxglgames.td;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.quest.Quest;
import com.almasb.fxgl.quest.QuestService;
import com.almasb.fxgl.quest.QuestState;
import com.almasb.fxgl.time.TimerAction;
import com.almasb.fxglgames.td.components.EnemyComponent;
import com.almasb.fxglgames.td.data.*;
import com.almasb.fxglgames.td.ui.*;
import com.almasb.fxglgames.td.ui.scene.TowerDefenseGameMenu;
import com.almasb.fxglgames.td.ui.scene.TowerDefenseMainMenu;
import javafx.beans.binding.Bindings;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.td.EntityType.ENEMY;
import static com.almasb.fxglgames.td.EntityType.WAY;
import static com.almasb.fxglgames.td.data.Config.*;
import static com.almasb.fxglgames.td.data.Vars.*;

/**
 * This is an example of a tower defense game.
 *
 * TODO:
 * - bonus buffs (runes)
 * - tower level up (upgrades)
 * - level end scene
 * - player progression currency, so that towers are unlocked via the "shop"
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseApp extends GameApplication {

    private List<TowerData> towerData;

    private LevelData currentLevel;

    private TowerSelectionBox towerSelectionBox;
    private TowerSellBox towerSellBox;
    private WaveIcon waveIcon;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Tower Defense");
        settings.setVersion("0.4");
        settings.setWidth(25 * 64);
        settings.setHeight(14 * 64);
        settings.getCSSList().add("main.css");
        settings.setIntroEnabled(false);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.addEngineService(QuestService.class);
        settings.addEngineService(CurrencyService.class);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new TowerDefenseMainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new TowerDefenseGameMenu();
            }
        });
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.L, () -> {
            byType(ENEMY).forEach(e -> e.removeFromWorld());

            spawnActions.forEach(a -> a.expire());

            currentLevel.waves(geti(CURRENT_WAVE))
                    .forEach(wave -> {
                        EnemyData data = getAssetLoader().loadJSON("enemies/" + wave.enemy(), EnemyData.class).get();

                        int reward = data.reward() * wave.amount();

                        inc(MONEY, reward);
                    });

            set(NUM_ENEMIES, 0);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(NUM_ENEMIES, 0);
        vars.put(PLAYER_HP, STARTING_HP);
        vars.put(CURRENT_WAVE, 0);
        vars.put(MONEY, currentLevel.startMoney());
        vars.put(NUM_TOWERS, 0);
    }

    public void setCurrentLevel(LevelData currentLevel) {
        this.currentLevel = currentLevel;
    }

    @Override
    protected void initGame() {
        initVarListeners();

        loadTowerData();

        getGameWorld().addEntityFactory(new TowerDefenseFactory());

        // construct UI objects
        towerSelectionBox = new TowerSelectionBox(towerData);
        towerSellBox = new TowerSellBox();
        waveIcon = new WaveIcon();

        loadCurrentLevel();
    }

    private void initVarListeners() {
        getWorldProperties().<Integer>addListener(NUM_ENEMIES, (old, newValue) -> {
            if (newValue == 0) {
                onWaveEnd();
            }
        });

        getWorldProperties().<Integer>addListener(PLAYER_HP, (old, newValue) -> {
            if (newValue == 0) {
                gameOver();
            }
        });

        getWorldProperties().<Integer>addListener(CURRENT_WAVE, (old, newValue) -> {
            Animations.playWaveIconAnimation(waveIcon);
        });

        // TODO: add FXGL API for limiting values of properties / vars?
        getWorldProperties().<Integer>addListener(MONEY, (old, newValue) -> {
            if (newValue > MAX_MONEY) {
                set(MONEY, MAX_MONEY);
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
                .toList();
    }

    private void loadCurrentLevel() {
        set(CURRENT_WAVE, 0);

        waveIcon.setMaxWave(currentLevel.maxWaveIndex());

        runOnce(() -> {
            //initQuests(currentLevel);
        }, Duration.seconds(0.1));

        setLevelFromMap("tmx/" + currentLevel.map());

        getGameWorld().getEntitiesFiltered(e -> e.isType("TiledMapLayer"))
                .forEach(e -> {
                    e.getViewComponent().addOnClickHandler(event -> {
                        towerSelectionBox.setVisible(false);
                    });
                });

        scheduleNextWave();
    }

    private void initQuests(LevelData level) {
        //getService(QuestService.class).questsProperty().forEach(quest -> getService(QuestService.class).removeQuest(quest));

        var quest = new Quest("Objectives");

        quest.stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("QUEST: " + oldValue + " -> " + newValue);
        });

        level.quests()
                .forEach(data -> {
                    quest.addIntObjective(data.desc(), data.varName(), (int) data.varValue());
                });

        getQuestService().startQuest(quest);

        var vbox = new VBox(10);
        vbox.getChildren().addAll(
                getUIFactoryService().newText(quest.getName(), Color.ANTIQUEWHITE, 24.0)
        );

        quest.objectivesProperty().forEach(obj -> {
            var text = getUIFactoryService().newText("");
            text.textProperty().bind(
                    Bindings.when(obj.stateProperty().isEqualTo(QuestState.COMPLETED))
                            .then("- " + obj.getDescription() + " \u2713")
                            .otherwise("- " + obj.getDescription())
            );
            text.fillProperty().bind(
                    Bindings.when(obj.stateProperty().isEqualTo(QuestState.COMPLETED))
                            .then(Color.GREEN)
                            .otherwise(Color.WHITE)
            );

            vbox.getChildren().add(text);
        });

        addUINode(vbox, getAppWidth() - 200, 10);
    }

    private void scheduleNextWave() {
        waveIcon.scheduleWave(WAVE_PREP_TIME, () -> nextWave());
    }

    private void nextWave() {
        if (geti(CURRENT_WAVE) < currentLevel.maxWaveIndex()) {
            inc(CURRENT_WAVE, 1);

            currentLevel.waves(geti(CURRENT_WAVE))
                    .forEach(wave -> {
                        spawnWave(wave);
                    });
        }
    }

    private List<TimerAction> spawnActions = new ArrayList<>();

    private void spawnWave(WaveData wave) {
        var wayEntity = getGameWorld().getSingleton(e ->
                e.isType(WAY) && e.getString("name").equals(wave.way())
        );

        EnemyData data = getAssetLoader().loadJSON("enemies/" + wave.enemy(), EnemyData.class).get();

        Polyline p = wayEntity.getObject("polyline");
        var way = Way.fromPolyline(p, wayEntity.getX(), wayEntity.getY());

        spawnActions.clear();

        for (int i = 0; i < wave.amount(); i++) {
            var a = runOnce(() -> {
                spawnWithScale(
                        "Enemy",
                        new SpawnData()
                                .put("way", way)
                                .put("enemyData", data),
                        Duration.seconds(0.45),
                        Interpolators.ELASTIC.EASE_OUT()
                );

            }, Duration.seconds(i * wave.interval()));

            spawnActions.add(a);
        }

        inc(NUM_ENEMIES, wave.amount());
    }

    private void onWaveEnd() {
        currentLevel.waves(geti(CURRENT_WAVE))
                .forEach(wave -> {
                    inc(MONEY, wave.reward());
                });

        if (geti(CURRENT_WAVE) < currentLevel.maxWaveIndex()) {
            scheduleNextWave();
        } else {
            gameOver();
        }
    }

    @Override
    protected void initUI() {
        towerSelectionBox.setVisible(false);
        towerSellBox.setVisible(false);

        addUINode(towerSellBox);
        addUINode(towerSelectionBox);

        addUINode(new MoneyIcon(), 10, 10);
        addUINode(new HPIcon(), 10, 90);

        addUINode(waveIcon, 10, 250);

        addUINode(new EnemiesIcon(), 10, 170);
    }

    public void onCellClicked(Entity cell) {
        // if we already have a tower on this tower base, open tower selling box
        if (cell.getProperties().exists("tower")) {

            // TODO: duplicate code as below
            towerSelectionBox.setVisible(false);

            towerSellBox.setCell(cell);
            towerSellBox.setVisible(true);

            var x = cell.getX() > getAppWidth() / 2.0 ? cell.getX() - 80 : cell.getX();

            towerSellBox.setTranslateX(x);
            towerSellBox.setTranslateY(cell.getY());

            return;
        }

        towerSellBox.setVisible(false);

        towerSelectionBox.setCell(cell);
        towerSelectionBox.setVisible(true);

        var x = cell.getX() > getAppWidth() / 2.0 ? cell.getX() - 250 : cell.getX();

        towerSelectionBox.setTranslateX(x);
        towerSelectionBox.setTranslateY(cell.getY());
    }

    public void onTowerSelected(Entity cell, TowerData data) {
        if (geti(MONEY) - data.cost() >= 0) {
            towerSelectionBox.setVisible(false);

            inc(MONEY, -data.cost());

            var tower = spawnWithScale(
                    "Tower",
                    new SpawnData(cell.getPosition()).put("towerData", data),
                    Duration.seconds(0.85),
                    Interpolators.ELASTIC.EASE_OUT()
            );

            tower.getViewComponent().addOnClickHandler(e -> {
                if (!tower.getBoolean("isDespawning")) {
                    onCellClicked(cell);
                }
            });

            cell.setProperty("tower", tower);

            inc(NUM_TOWERS, +1);
        }
    }

    public void onTowerSold(Entity cell) {
        towerSellBox.setVisible(false);

        Entity tower = cell.getObject("tower");

        TowerData data = tower.getObject("towerData");

        // TODO: record, then check wave spawned, if this wave, then refund 100%
        inc(MONEY, (int)(data.cost() * COST_REFUND_ON_SALE));

        cell.getProperties().remove("tower");

        tower.setProperty("isDespawning", true);

        despawnWithScale(
                tower,
                Duration.seconds(0.5),
                Interpolators.EXPONENTIAL.EASE_OUT()
        );
    }

    public void killEnemy(Entity enemy) {
        inc(MONEY, enemy.getComponent(EnemyComponent.class).getData().reward());
        inc(NUM_ENEMIES, -1);

        enemy.removeFromWorld();
    }

    public void onEnemyReachedEnd(Entity enemy) {
        inc(PLAYER_HP, -1);
        inc(NUM_ENEMIES, -1);
    }

    private void gameOver() {
        showMessage("Level Complete!", getGameController()::gotoMainMenu);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
