package com.almasb.fxglgames.gravity;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.parser.LevelParser;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GravityApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Gravity");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        LevelParser levelParser = new TextLevelParser(getGameWorld().getEntityFactory());
        getGameWorld().setLevel(levelParser.parse("levels/level0.txt"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
