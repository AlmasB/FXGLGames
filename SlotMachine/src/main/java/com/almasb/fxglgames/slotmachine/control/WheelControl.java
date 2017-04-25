package com.almasb.fxglgames.slotmachine.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.slotmachine.SlotMachineApp;

import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WheelControl extends AbstractControl {

    // [0..9]
    private int value = 0;
    private int speed = 10;
    private boolean spinning = false;

    private Random random = new Random();

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
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
