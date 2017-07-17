/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.listener.StateListener;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxgl.ui.UIController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PacmanUIController implements UIController, StateListener {

    @FXML
    private Pane root;

    private ProgressBar timeBar;

    @FXML
    private Label labelScore;

    @FXML
    private Label labelTeleport;

    public Label getLabelScore() {
        return labelScore;
    }

    public Label getLabelTeleport() {
        return labelTeleport;
    }

    @Override
    public void init() {
        timeBar = new ProgressBar(false);
        timeBar.setHeight(50);
        timeBar.setTranslateX(0);
        timeBar.setTranslateY(100);
        timeBar.setRotate(-90);
        timeBar.setFill(Color.GREEN);
        timeBar.setLabelVisible(false);
        timeBar.setMaxValue(PacmanApp.TIME_PER_LEVEL);
        timeBar.setMinValue(0);
        timeBar.setCurrentValue(PacmanApp.TIME_PER_LEVEL);
        timeBar.currentValueProperty().bind(FXGL.getApp().getGameState().intProperty("time"));

        root.getChildren().addAll(timeBar);

        labelScore.setFont(FXGL.getUIFactory().newFont(24));
        labelTeleport.setFont(FXGL.getUIFactory().newFont(24));
    }

    @Override
    public void onUpdate(double tpf) {

    }
}
