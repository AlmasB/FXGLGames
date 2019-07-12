package com.almasb.fxglgames.mario;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxglgames.mario.components.HPComponent;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.geto;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class DeveloperActions {

    public static void add(Input input) {
        input.addAction(new UserAction("Decrease HP") {
            @Override
            protected void onActionBegin() {
                Entity player = geto("player");
                player.getComponent(HPComponent.class).setValue(player.getComponent(HPComponent.class).getValue() - 10);
            }
        }, KeyCode.G);

        input.addAction(new UserAction("Increase HP") {
            @Override
            protected void onActionBegin() {
                Entity player = geto("player");
                player.getComponent(HPComponent.class).setValue(player.getComponent(HPComponent.class).getValue() + 10);
            }
        }, KeyCode.H);
    }
}
