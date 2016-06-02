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

package com.almasb.mario;

import com.almasb.ents.Entity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;

public class GameState {

    private MarioApp app;

    private UIOverlay ui;

    private IntegerProperty score = new SimpleIntegerProperty();

    private IntegerProperty lives = new SimpleIntegerProperty(3);

    private IntegerProperty ghostBombs = new SimpleIntegerProperty();

    private Point2D checkpoint;

    public GameState(MarioApp app, UIOverlay ui) {
        this.app = app;
        this.ui = ui;

        ui.bindTextGhostBombs(ghostBombs);
        ui.bindTextLives(lives);
        ui.bindTextScore(score);

        lives.addListener((obs, oldLives, newLives) -> {
            //if (newLives.intValue() == 0)
                //app.getSceneManager().fireFXGLEvent(new FXGLEvent(Event.GAME_OVER), Type.PLAYER);
        });

        score.addListener((obs, oldScore, newScore) -> {
            if (newScore.intValue() % 5000 == 0)
                addLife();
        });
    }

    public boolean hasGhostBombs() {
        return ghostBombs.get() > 0;
    }

    public void addScoreFromCoin(Entity coin) {
        //ui.playScoreAnimation(100, coin.localToScene(0, 0), () -> score.set(score.get() + 100));
    }

    public void addScoreFromEnemyKill(Entity enemy) {
        //ui.playScoreAnimation(500, enemy.localToScene(0, 0), () -> score.set(score.get() + 500));
    }

    public void addLife() {
        ui.playLifePulseAnimation(() -> lives.set(lives.get() + 1));
    }

    public void loseLife() {
        ui.playLifeDropAnimation(() -> lives.set(lives.get() - 1));
    }

    public void addGhostBomb() {
        ghostBombs.set(ghostBombs.get() + 1);
    }

    public void removeGhostBomb() {
        ghostBombs.set(ghostBombs.get() - 1);
    }

    public void updateCheckpoint(Point2D p) {
        if (checkpoint != null) {
            ui.showMessage("CHECKPOINT!");
        }
        checkpoint = p;
    }

    public Point2D getCheckpoint() {
        return checkpoint;
    }

    public void gameWin() {
        //app.pause();
        ui.showMessage("YOU HAVE WON!");
    }

    public void gameLose() {
        //app.pause();
        ui.showMessage("DEMO OVER!");
    }
}
