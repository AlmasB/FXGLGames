package com.almasb.fxglgames.bomberman.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxglgames.bomberman.BombermanApp;
import com.almasb.fxglgames.bomberman.BombermanType;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BombComponent extends Component {

    private int radius;

    public BombComponent(int radius) {
        this.radius = radius;
    }

    public void explode() {
        BoundingBoxComponent bbox = entity.getBoundingBoxComponent();

        getGameWorld()
                .getEntitiesInRange(bbox.range(radius, radius))
                .stream()
                .filter(e -> e.isType(BombermanType.BRICK))
                .forEach(e -> {
                    FXGL.<BombermanApp>getAppCast().onBrickDestroyed(e);
                    e.removeFromWorld();
                });

        entity.removeFromWorld();
    }
}
