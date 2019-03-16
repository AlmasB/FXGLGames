package com.almasb.fxglgames.slotmachine.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxglgames.slotmachine.SlotMachineApp;

import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WheelComponent extends Component {

    // [0..9]
    private int value = 0;
    private int speed = 10;
    private boolean spinning = false;

    private Random random = new Random();

    private TransformComponent position;

    @Override
    public void onUpdate(double tpf) {
        if (!spinning)
            return;

        double newPosition = value * 260;
        double diff = -newPosition - (position.getY() - 70);

        if (diff > 0) {
            position.translateY(speed);
        } else if (diff < 0) {
            position.translateY(-speed);
        } else {
            spinning = false;
            FXGL.<SlotMachineApp>getAppCast().onSpinFinished(value);
        }
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void spin() {
        spinning = true;
        value = random.nextInt(10);

        // slightly better chances for player
        // since we have two same sprites within the wheel
        if (value == 6)
            value = 4;
    }
}
