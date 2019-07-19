package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyTurretComponent extends Component {

    private LocalTimer shootTimer;

    private Entity player;

    @Override
    public void onAdded() {
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) {
            player = FXGL.geto("player");
        }

        if (shootTimer.elapsed(Duration.seconds(2))) {
            if (player != null) {
                FXGL.spawn("enemyAttackZombieProjectile", new SpawnData(entity.getPosition()).put("direction", player.getPosition().subtract(entity.getPosition())));
            }
            shootTimer.capture();
        }
    }
}
