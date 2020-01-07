package com.almasb.fxglgames.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RTSApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL RTS Game");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
