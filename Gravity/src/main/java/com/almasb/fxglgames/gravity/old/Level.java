package com.almasb.fxglgames.gravity.old;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Level {

    private static final int SPACE = 0,
            PLATFORM = 1,
            ENEMY = 2,
            COIN = 3,
            POWERUP_HP = 4,
            POWERUP_G = 5,
            STONE = 6,
            PLAYER = 7,
            PORTAL = 8;

    private int[][] grid;

    public final ObservableList<GameObject> gameObjects = FXCollections.<GameObject>observableArrayList();

    public Level(List<String> lines) {
        GameEnvironment.resetWorld();

        grid = new int[lines.get(0).length()][lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                grid[j][i] = Integer.parseInt(String.valueOf(line.charAt(j)));
            }
        }

        parse();
    }

    public int getWidth() {
        return grid.length * Config.BLOCK_SIZE;
    }

    public int getHeight() {
        return grid[0].length * Config.BLOCK_SIZE;
    }

    private void parse() {
        createLevelBounds();

        for (int i = 0; i < grid[0].length; i++) {
            int x = -1, y = -1, w = 0;

            for (int j = 0; j < grid.length; j++) {
                if (grid[j][i] == PLATFORM) {
                    if (w == 0) {
                        x = j * Config.BLOCK_SIZE;
                        y = i * Config.BLOCK_SIZE;
                        w = Config.BLOCK_SIZE;
                    }
                    else {
                        w += Config.BLOCK_SIZE;
                    }
                }
                else {
                    if (w != 0) {
                        gameObjects.add(new Platform(x, y, w, Config.BLOCK_SIZE));
                        w = 0;
                    }
                }

                switch (grid[j][i]) {
                    case ENEMY:
                        gameObjects.add(new Enemy(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE));
                        break;
                    case COIN:
                        gameObjects.add(new Coin(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE));
                        break;
                    case STONE:
                        gameObjects.add(new Stone(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE));
                        break;
                    case POWERUP_HP:
                        gameObjects.add(new Powerup(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE, Powerup.PowerType.HP));
                        break;
                    case POWERUP_G:
                        gameObjects.add(new Powerup(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE, Powerup.PowerType.GRAVITY));
                        break;
                    case PLAYER:
                        GameEnvironment.setPlayer(new Player(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE));
                        gameObjects.add(GameEnvironment.getPlayer());
                        break;
                    case PORTAL:
                        gameObjects.add(new Portal(j*Config.BLOCK_SIZE, i*Config.BLOCK_SIZE));
                        break;
                }
            }

            if (w != 0) {
                gameObjects.add(new Platform(x, y, w, Config.BLOCK_SIZE));
                w = 0;
            }
        }
    }

    private void createLevelBounds() {
        // top
        gameObjects.add(new Platform(0, 0, getWidth(), Config.BLOCK_SIZE));

        // left
        gameObjects.add(new Platform(0, Config.BLOCK_SIZE, Config.BLOCK_SIZE, getHeight() - Config.BLOCK_SIZE));

        // right
        gameObjects.add(new Platform(grid.length * Config.BLOCK_SIZE - Config.BLOCK_SIZE, Config.BLOCK_SIZE, Config.BLOCK_SIZE, getHeight() - Config.BLOCK_SIZE));

        // bot
        gameObjects.add(new Spike(Config.BLOCK_SIZE, getHeight() - Config.BLOCK_SIZE, getWidth() - Config.BLOCK_SIZE - Config.BLOCK_SIZE, Config.BLOCK_SIZE));
    }
}
