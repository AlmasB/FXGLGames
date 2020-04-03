package com.almasb.fxglgames.spaceinvaders.level.boss;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.spaceinvaders.components.HealthComponent;
import com.almasb.fxglgames.spaceinvaders.level.SpaceLevel;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class BossLevel extends SpaceLevel {

    private ProgressBar bossBar;

    public BossLevel() {

    }

    Entity spawnBoss(double x, double y, int hp, String textureName) {
        Entity boss = spawn("Boss", new SpawnData(x, y).put("hp", hp).put("textureName", textureName));

        addEnemy(boss);

        animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(FXGLMath.random(0.0, 1.0) * 2))
                .scale(boss)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();



        // hp bar

        HealthComponent hpComponent = boss.getComponent(HealthComponent.class);

        bossBar = ProgressBar.makeHPBar();
        bossBar.setFill(Color.RED);
        bossBar.setTranslateX(100);
        bossBar.setTranslateY(70);
        bossBar.setWidth(FXGL.getAppWidth() - 100 * 2);
        bossBar.setHeight(30);
        bossBar.setLabelVisible(false);

        bossBar.setMaxValue(hpComponent.getValue());
        bossBar.currentValueProperty().bind(hpComponent.valueProperty());

        FXGL.getGameScene().addUINode(bossBar);

        // text fight

//        Text text = FXGL.getUIFactory().newText("Boss Fight!", Color.WHITE, 24.0);
//
//        FXGL.getGameScene().addUINode(text);
//
//        centerText(text);
//
//
//
//        scale(text, new Point2D(2, 2), Point2D.ZERO, Duration.ZERO, Duration.seconds(1), () -> {
//            FXGL.getGameScene().removeUINode(text);
//        }).startInPlayState();


        return boss;
    }

    @Override
    public void destroy() {
        FXGL.getGameScene().removeUINode(bossBar);
    }
}
