package com.almasb.spacerunner.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.spacerunner.SpaceRunnerFactory;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class PlayerControl extends AbstractControl {

    private PositionComponent position;

    private double speed;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = tpf * 60;

        position.translateX(speed);
    }

    public void up() {
        position.translateY(-speed);
    }

    public void down() {
        position.translateY(speed);
    }

    public void shoot() {
        Entity bullet = FXGL.getInstance(SpaceRunnerFactory.class)
                .newBullet(position.getX() + 40, position.getY() + 20);

        getEntity().getWorld().addEntity(bullet);
    }
}
