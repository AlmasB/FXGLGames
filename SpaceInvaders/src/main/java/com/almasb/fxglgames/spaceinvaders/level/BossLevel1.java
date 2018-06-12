package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.components.CircularMovementComponent;
import com.almasb.fxgl.extra.entity.components.HealthComponent;
import com.almasb.fxgl.ui.ProgressBar;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossLevel1 extends BossLevel {

    private List<Animation<?>> animations = new ArrayList<>();



    @Override
    public void init() {
        Entity boss = spawnBoss(Config.WIDTH * 2 / 3, Config.HEIGHT / 3, 50, "boss1.png");
        boss.addComponent(new CircularMovementComponent(2, 200));
    }

    @Override
    public void destroy() {


        animations.forEach(Animation::stop);
    }
}
