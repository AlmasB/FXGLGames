package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.newLocalTimer;
import static com.almasb.fxgl.dsl.FXGL.spawn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RandomAttackComponent extends Component {

    private TankViewComponent tank;
    private MoveComponent moveComponent;
    private LocalTimer attackTimer;
    private Duration attackInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));

    @Override
    public void onAdded() {
        attackTimer = newLocalTimer();
        attackTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (attackTimer.elapsed(attackInterval)) {

            // TODO: move to TankComponent ....
            spawn("Bullet", new SpawnData(entity.getCenter())
                    .put("direction", moveComponent.getMoveDir().vector)
                    .put("owner", entity)
            );

            attackInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));
            attackTimer.capture();
        }
    }
}
