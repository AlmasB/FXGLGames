package com.almasb.fxglgames.geowars.component.enemy;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BomberComponent extends Component {

    private static final int BOMBER_WIDTH = (int)(202 * 0.15);

    private ProjectileComponent projectile;

    @Override
    public void onUpdate(double tpf) {
        if (entity.getX() <= 0) {
            projectile.setDirection(new Point2D(1, 0));
        } else if (entity.getX() + BOMBER_WIDTH >= FXGL.getAppWidth()) {
            projectile.setDirection(new Point2D(-1, 0));
        }
    }
}
