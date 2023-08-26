package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import com.almasb.fxglgames.geowars.GeoWarsType;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class MineComponent extends Component {

    public void explode() {
        getGameWorld().getEntitiesInRange(entity.getBoundingBoxComponent().range(150, 150))
                .stream()
                .filter(e -> e.hasComponent(HealthIntComponent.class))
                .forEach(e -> FXGL.<GeoWarsApp>getAppCast().killEnemy(e));

        getGameWorld().getSingleton(GeoWarsType.GRID)
                .getComponent(GridComponent.class)
                .applyExplosiveForce(2500, entity.getCenter(), 150);
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
