package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxglgames.tictactoe.components.enemy.MinimaxComponent;
import com.almasb.fxglgames.tictactoe.event.AIEvent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * An example of a UI based game.
 * A classic game of Tic-tac-toe.
 * Comes with 2 types of AI: rule based and minimax based.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TicTacToeApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("TicTacToe");
        settings.setVersion("0.3");
        settings.setWidth(600);
        settings.setHeight(600);
    }

    private TileEntity[][] board = new TileEntity[3][3];
    private List<TileCombo> combos = new ArrayList<>();

    private boolean playerStarts = true;

    public TileEntity[][] getBoard() {
        return board;
    }

    public List<TileCombo> getCombos() {
        return combos;
    }

    private boolean firstTime = true;

    @Override
    protected void initGame() {
        if (firstTime) {
            getEventBus().addEventHandler(AIEvent.MOVED, event -> checkGameFinished());
            firstTime = false;
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                TileEntity tile = new TileEntity(x * getAppWidth() / 3, y * getAppHeight() / 3);
                board[x][y] = tile;

                getGameWorld().addEntity(tile);
            }
        }

        Entity enemy = new Entity();

        // this controls the AI behavior
        enemy.addComponent(new MinimaxComponent());
        getGameWorld().addEntity(enemy);

        combos.clear();

        // horizontal
        for (int y = 0; y < 3; y++) {
            combos.add(new TileCombo(board[0][y], board[1][y], board[2][y]));
        }

        // vertical
        for (int x = 0; x < 3; x++) {
            combos.add(new TileCombo(board[x][0], board[x][1], board[x][2]));
        }

        // diagonals
        combos.add(new TileCombo(board[0][0], board[1][1], board[2][2]));
        combos.add(new TileCombo(board[2][0], board[1][1], board[0][2]));

        if (playerStarts) {
            playerStarts = false;
        } else {
            aiMove();
            playerStarts = true;
        }
    }

    @Override
    protected void initUI() {
        Line line1 = new Line(getAppWidth() / 3, 0, getAppWidth() / 3, 0);
        Line line2 = new Line(getAppWidth() / 3 * 2, 0, getAppWidth() / 3 * 2, 0);
        Line line3 = new Line(0, getAppHeight() / 3, 0, getAppHeight() / 3);
        Line line4 = new Line(0, getAppHeight() / 3 * 2, 0, getAppHeight() / 3 * 2);

        getGameScene().addUINodes(line1, line2, line3, line4);

        // animation
        KeyFrame frame1 = new KeyFrame(Duration.seconds(0.5),
                new KeyValue(line1.endYProperty(), getAppHeight()));

        KeyFrame frame2 = new KeyFrame(Duration.seconds(1),
                new KeyValue(line2.endYProperty(), getAppHeight()));

        KeyFrame frame3 = new KeyFrame(Duration.seconds(0.5),
                new KeyValue(line3.endXProperty(), getAppWidth()));

        KeyFrame frame4 = new KeyFrame(Duration.seconds(1),
                new KeyValue(line4.endXProperty(), getAppWidth()));

        Timeline timeline = new Timeline(frame1, frame2, frame3, frame4);
        timeline.play();
    }

    private boolean checkGameFinished() {
        for (TileCombo combo : combos) {
            if (combo.isComplete()) {
                playWinAnimation(combo);
                return true;
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                TileEntity tile = board[x][y];
                if (tile.getValue() == TileValue.NONE) {
                    // at least 1 tile is empty
                    return false;
                }
            }
        }

        gameOver("DRAW");
        return true;
    }

    private void playWinAnimation(TileCombo combo) {
        Line line = new Line();
        line.setStartX(combo.getTile1().getCenter().getX());
        line.setStartY(combo.getTile1().getCenter().getY());
        line.setEndX(combo.getTile1().getCenter().getX());
        line.setEndY(combo.getTile1().getCenter().getY());
        line.setStroke(Color.YELLOW);
        line.setStrokeWidth(3);

        getGameScene().addUINode(line);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                new KeyValue(line.endXProperty(), combo.getTile3().getCenter().getX()),
                new KeyValue(line.endYProperty(), combo.getTile3().getCenter().getY())));
        timeline.setOnFinished(e -> gameOver(combo.getWinSymbol()));
        timeline.play();
    }

    private void gameOver(String winner) {
        getDisplay().showConfirmationBox("Winner: " + winner + "\nContinue?", yes -> {
            if (yes)
                getGameController().startNewGame();
            else
                getGameController().exit();
        });
    }

    public void onUserMove(TileEntity tile) {
        boolean ok = tile.getControl().mark(TileValue.X);

        if (ok) {
            boolean over = checkGameFinished();

            if (!over) {
                aiMove();
            }
        }
    }

    private void aiMove() {
        getEventBus().fireEvent(new AIEvent(AIEvent.WAITING));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
