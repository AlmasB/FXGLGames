package com.almasb.fxglgames.spaceinvaders.level.boss;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spaceinvaders.Config;
import com.almasb.fxglgames.spaceinvaders.components.HealthComponent;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.play;
import static com.almasb.fxgl.dsl.FXGL.runOnce;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BossLevelFinal extends BossLevel {

    private Entity boss;

    @Override
    public void playInCutscene(Runnable onFinished) {
        boss = spawnBoss(Config.WIDTH / 2 - 100, 50, 50, "boss_final.png");

        showStoryPane();

        updateAlienStoryText("This is the end!");

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();
        }, Duration.seconds(4));
    }

    @Override
    public void init() {
        boss.addComponent(new Level3BossComponent());
    }

    @Override
    public void playOutCutscene(Runnable onFinished) {
        showStoryPane();

        updateAlienStoryText("Thanks for watching! Contributions are welcome.");

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();
        }, Duration.seconds(7));
    }

    private static class Level3BossComponent extends Component {

        private LocalTimer invisTimer = FXGL.newLocalTimer();
        private LocalTimer attackTimer = FXGL.newLocalTimer();
        private Duration nextAttack = Duration.seconds(0.25);

        private LocalTimer hpTimer = FXGL.newLocalTimer();

        private boolean movingRight = true;

        @Override
        public void onUpdate(double tpf) {
            if (movingRight) {
                entity.translateX(tpf * 100);
            } else {
                entity.translateX(-tpf * 100);
            }

            if (entity.getX() < 50) {
                movingRight = true;
            }

            if (entity.getRightX() > FXGL.getAppWidth() - 50) {
                movingRight = false;
            }

//            if (invisTimer.elapsed(Duration.seconds(25))) {
//                if (entity.getView().isVisible()) {
//                    entity.getComponent(CollidableComponent.class).setValue(false);
//                    entity.getView().setVisible(false);
//
//                    Entity e = spawn("Boss", new SpawnData(FXGLMath.random(0, FXGL.getAppWidth() - 202), 50).put("hp", 20).put("textureName", "boss_final.png"));
//                    e.setOnNotActive(() -> {
//                        entity.getComponent(CollidableComponent.class).setValue(true);
//                        entity.getView().setVisible(true);
//                    });
//                }
//
//                invisTimer.capture();
//            }
//
//            if (entity.getView().isVisible()) {
//                if (attackTimer.elapsed(nextAttack)) {
//                    shoot();
//
//                    nextAttack = Duration.seconds(0.25);
//                    attackTimer.capture();
//                }
//            }

            if (hpTimer.elapsed(Duration.seconds(2))) {
                HealthComponent hp = entity.getComponent(HealthComponent.class);

                if (hp.getValue() < 50) {
                    hp.setValue(hp.getValue() + 1);
                }

                hpTimer.capture();
            }
        }

        private void shoot() {
            GameWorld world = getEntity().getWorld();
            Entity bullet = world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

            play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
        }
    }
}
