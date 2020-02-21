package com.almasb.fxglgames.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.MouseButton;

import static com.almasb.fxgl.dsl.FXGL.*;

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

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {

        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new RTSFactory());

        spawn("unit", 300, 300);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
