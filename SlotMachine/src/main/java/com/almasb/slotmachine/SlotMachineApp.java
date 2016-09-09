package com.almasb.slotmachine;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineApp extends GameApplication {

    private SlotMachineFactory entityFactory;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Slot Machine");
        settings.setVersion("0.1");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initAssets() {

    }

    @Override
    protected void initGame() {
        entityFactory = new SlotMachineFactory();


        getGameWorld().addEntities(entityFactory.buildWheels());
        getGameWorld().addEntity(entityFactory.buildBackground());

        getGameWorld().addEntity(entityFactory.buildLever());
    }

    @Override
    protected void initPhysics() {

    }

    private SlotMachineController controller;

    @Override
    protected void initUI() {
//        controller = new SlotMachineController();
//        Node ui = getAssetLoader().loadFXML("main_ui.fxml", controller);
//
//        getGameScene().addUINode(ui);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
