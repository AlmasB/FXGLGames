package com.almasb.fxglgames.mario;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyZombieComponent extends Component {

    private AnimatedTexture texture;

    private AnimationChannel animWalk;

    private int patrolEndX;
    private boolean goingRight = true;

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private double speed;

    public EnemyZombieComponent(int patrolEndX) {
        this.patrolEndX = patrolEndX;

        duration = Duration.seconds(2);

        int w = 1392 / 4;
        int h = 390 / 4;

        animWalk = new AnimationChannel(FXGL.image("enemies/zombie/zombie1.png", w, h), 6, 232 / 4, h, Duration.seconds(0.75), 0, 5);

        texture = new AnimatedTexture(animWalk);
        texture.loop();
    }

    @Override
    public void onAdded() {
        distance = patrolEndX - entity.getX();

        timer = FXGL.newLocalTimer();
        timer.capture();
        speed = distance / duration.toSeconds();

        entity.getTransformComponent().setScaleOrigin(new Point2D(232 / 4 / 2, 390 / 4 / 2));
        entity.setView(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(duration)) {
            goingRight = !goingRight;
            timer.capture();
        }

        entity.translateX(goingRight ? speed * tpf : -speed * tpf);
        entity.setScaleX(goingRight ? 1 : -1);
    }
}
