package com.almasb.fxglgames.tanks.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RandomMoveComponent extends Component {

    private MoveComponent moveComponent;
    private LocalTimer moveTimer;
    private Duration moveInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));

    private MoveDirection direction = FXGLMath.random(MoveDirection.values()).get();

    @Override
    public void onAdded() {
        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        moveComponent.setMoveDirection(direction);

        if (moveTimer.elapsed(moveInterval)) {
            moveInterval = Duration.seconds(FXGLMath.random(0.25, 3.0));
            direction = FXGLMath.random(MoveDirection.values()).get();
            moveTimer.capture();
        }
    }
}
