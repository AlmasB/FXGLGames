package com.almasb.spacerunner;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpaceRunnerApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Runner");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initAssets() {

    }

    @Override
    protected void initGame() {

    }

    @Override
    protected void initPhysics() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
