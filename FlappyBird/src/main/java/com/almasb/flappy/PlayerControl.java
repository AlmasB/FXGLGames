package com.almasb.flappy;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import org.jbox2d.common.Vec2;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {

    private Vec2 acceleration = new Vec2(6, 0);

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        acceleration.y += tpf * 10;

        if (acceleration.y < -5)
            acceleration.y = -5;

        if (acceleration.y > 5)
            acceleration.y = 5;

        position.translate(acceleration.x, acceleration.y);
    }

    public void jump() {
        acceleration.addLocal(0, -5);
    }
}
