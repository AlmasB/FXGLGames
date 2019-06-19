package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RicochetComponent extends Component {

    private BoundingBoxComponent bbox;

    @Override
    public void onUpdate(double tpf) {

        if (bbox.getMinXWorld() < 0 || bbox.getMaxXWorld() > FXGL.getAppWidth()
                || bbox.getMinYWorld() < 0 || bbox.getMaxYWorld() > FXGL.getAppHeight()) {

            Point2D direction = FXGL.<GeoWarsApp>getAppCast()
                    .getPlayer()
                    .getCenter()
                    .subtract(bbox.getCenterWorld());

            entity.getComponent(ProjectileComponent.class).setDirection(direction);
        }
    }
}
