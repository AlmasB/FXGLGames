package com.almasb.spacerunner.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.spacerunner.EntityType;
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
        speed = tpf * 300;

        position.translateX(tpf * 60);
    }

    public void up() {
//        if (position.getY() - speed <= 0) {
//            position.setY(0);
//            return;
//        }

        position.translateY(-speed);
    }

    public void down() {
//        if (position.getY() + speed <= FXGL.getSettings().getHeight()) {
//            position.setY(FXGL.getSettings().getHeight());
//            return;
//        }

        position.translateY(speed);
    }

    public void shoot() {
        Entity bullet = FXGL.getInstance(SpaceRunnerFactory.class)
                .newBullet(position.getX() + 40, position.getY() + 20, EntityType.PLAYER);

        getEntity().getWorld().addEntity(bullet);
    }
}
