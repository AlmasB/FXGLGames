package com.almasb.fxglgames.tictactoe;

import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxglgames.tictactoe.ai.MinimaxService;
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
        settings.setVersion("1.0");
        settings.setWidth(600);
        settings.setHeight(600);
        settings.addEngineService(MinimaxService.class);
    }

    private Entity[][] board = new Entity[3][3];
    private List<TileCombo> combos = new ArrayList<>();

    private boolean playerStarts = true;

    public Entity[][] getBoard() {
        return board;
    }

    public List<TileCombo> getCombos() {
        return combos;
    }

    @Override
    protected void onPreInit() {
        getEventBus().addEventHandler(AIEvent.MOVED, event -> checkGameFinished());
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TicTacToeFactory());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                board[x][y] = spawn("tile", x * getAppWidth() / 3, y * getAppHeight() / 3);
            }
        }

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
                Entity tile = board[x][y];
                if (tile.getComponent(TileViewComponent.class).isEmpty()) {
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
        getDialogService().showConfirmationBox("Winner: " + winner + "\nContinue?", yes -> {
            if (yes)
                getGameController().startNewGame();
            else
                getGameController().exit();
        });
    }

    public void onUserMove(Entity tile) {
        boolean ok = tile.getComponent(TileViewComponent.class).mark(TileValue.X);

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
