package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyAttackZombieComponent extends Component {

    private AnimatedTexture texture;

    private AnimationChannel animAttack;

    private Entity player;

    public EnemyAttackZombieComponent() {
        int w = 1470 / 4;
        int h = 352 / 4;

        animAttack = new AnimationChannel(FXGL.image("enemies/zombie/zombie_attack.png", w, h), 6, 245 / 4, h, Duration.seconds(1.35), 0, 5);

        texture = new AnimatedTexture(animAttack);
        texture.loop();
    }

    @Override
    public void onAdded() {
        entity.getTransformComponent().setScaleOrigin(new Point2D(245 / 4 / 2, 352 / 4 / 2));
        entity.getViewComponent().addChild(texture);

        texture.setOnCycleFinished(() -> {
            if (player != null) {
                FXGL.spawn("enemyAttackZombieProjectile", new SpawnData(entity.getPosition()).put("direction", player.getPosition().subtract(entity.getPosition())));
            }
        });
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) {
            player = FXGL.geto("player");
        }

        entity.setScaleX(player.getX() < entity.getX() ? -1 : 1);
    }
}
