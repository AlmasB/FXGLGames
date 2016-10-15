package com.almasb.spacerunner.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.spacerunner.EntityType;
import com.almasb.spacerunner.SpaceRunnerFactory;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    private LocalTimer attackTimer;
    private Duration nextAttack = Duration.seconds(2);

    private AudioPlayer audioPlayer;

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        audioPlayer = FXGL.getService(ServiceType.AUDIO_PLAYER);

        attackTimer = FXGL.getService(ServiceType.LOCAL_TIMER);
        attackTimer.capture();

        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (attackTimer.elapsed(nextAttack)) {
            if (Math.random() < 0.8) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        position.translateX(tpf * 30);
    }

    private void shoot() {
        Entity bullet = FXGL.getInstance(SpaceRunnerFactory.class)
                .newBullet(position.getX(), position.getY() + 20, EntityType.ENEMY);

        getEntity().getWorld().addEntity(bullet);

        //audioPlayer.playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}

