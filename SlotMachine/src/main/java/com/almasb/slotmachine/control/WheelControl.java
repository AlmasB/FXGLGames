package com.almasb.slotmachine.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.DoubleComponent;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;

import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WheelControl extends AbstractControl {

    // [0..9]
    private int value = 0;
    private int speed = 10;

    private Random random = new Random();

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        double newPosition = value * 260;
        double diff = -newPosition - (position.getY() - 70);

        if (diff > 0) {
            position.translateY(speed);
        } else if (diff < 0) {
            position.translateY(-speed);
        }
    }

    public void spin() {
        value = random.nextInt(10);
    }
}
