package com.almasb.fxglgames.spacerunner.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.spacerunner.GameConfig;
import com.almasb.fxglgames.spacerunner.ai.AIPointComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveComponent extends Component {

    private Entity target;
    private boolean isMoving = false;
    private Point2D vector = Point2D.ZERO;

    private double tpf = 0.016;

    @Override
    public void onUpdate(double tpf) {
        this.tpf = tpf;

        if (target != null) {
            if (entity.distance(target) <= 30) {
                entity.setPosition(target.getPosition());

                isMoving = false;
                target = null;
            } else {
                entity.translate(vector);
            }
        }

        entity.translateX(tpf * 600);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void moveTo(Entity e, double speed) {
        target = e;
        vector = e.getPosition().subtract(entity.getPosition()).normalize().multiply(speed * tpf);

        //System.out.println(vector.magnitude());

        isMoving = true;
    }
}
