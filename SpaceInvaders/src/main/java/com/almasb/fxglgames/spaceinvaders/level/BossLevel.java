package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.components.CircularMovementComponent;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossLevel extends SpaceLevel {

    private List<Animation<?>> animations = new ArrayList<>();

    @Override
    public void init() {
        Entity boss = spawnBoss(Config.WIDTH * 2 / 3, Config.HEIGHT / 3);
        boss.addComponent(new CircularMovementComponent(2, 200));

        HealthComponent hp = boss.getComponent(HealthComponent.class);

        ProgressBar bossBar = ProgressBar.makeHPBar();
        bossBar.setFill(Color.RED);
        bossBar.setTranslateX(100);
        bossBar.setTranslateY(70);
        bossBar.setWidth(FXGL.getAppWidth() - 100 * 2);
        bossBar.setHeight(30);
        bossBar.setLabelVisible(false);

        bossBar.setMaxValue(hp.getValue());
        bossBar.currentValueProperty().bind(hp.valueProperty());

        FXGL.getApp().getGameScene().addUINode(bossBar);
    }

    @Override
    public void destroy() {
        animations.forEach(Animation::stop);
    }
}
