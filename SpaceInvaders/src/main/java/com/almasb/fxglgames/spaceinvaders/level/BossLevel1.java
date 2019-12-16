package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossLevel1 extends BossLevel {

    private Entity boss;

    @Override
    public void playInCutscene(Runnable onFinished) {
        boss = spawnBoss(Config.WIDTH * 2 / 3, Config.HEIGHT / 3, 50, "boss1.png");

        showStoryPane();

        updateAlienStoryText("I will end you!");

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();
        }, Duration.seconds(4));
    }

    @Override
    public void init() {
        //boss.addComponent(new CircularMovementComponent(2, 200));
        boss.addComponent(new Level1BossComponent());
    }

    private static class Level1BossComponent extends Component {

        private LocalTimer attackTimer = FXGL.newLocalTimer();
        private Duration nextAttack = Duration.seconds(1);

        @Override
        public void onUpdate(double tpf) {
            if (attackTimer.elapsed(nextAttack)) {
                shoot();

                nextAttack = Duration.seconds(1);
                attackTimer.capture();
            }
        }

        private void shoot() {
            for (int i = 0; i < 6; i++) {
                GameWorld world = getEntity().getWorld();
                Entity bullet = world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

                bullet.translateX(20 * (i-3));
            }

            play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
        }
    }
}
