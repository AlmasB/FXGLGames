package com.almasb.fxglgames.cc;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalApp extends GameApplication {

    private enum Type {
        PLAYER, CRYSTAL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Crystal Chase");
    }

    @Override
    protected void initGame() {
        run(this::spawnCrystal, Duration.seconds(1));
    }

    private void spawnCrystal() {
        var texture = texture("YellowCrystal.png");


        entityBuilder().at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                .type(Type.CRYSTAL)
                .view(texture.toAnimatedTexture(8, Duration.seconds(0.2)).loop())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
