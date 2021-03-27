package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MineComponent extends Component {

    private Texture overlay;
    private LocalTimer beepTimer = newLocalTimer();

    @Override
    public void onAdded() {
        overlay = texture("mine_red.png", 315 * 0.25, 315 * 0.25);

        beepTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (beepTimer.elapsed(Duration.seconds(0.25))) {

            if (overlay.getParent() == null) {
                entity.getViewComponent().addChild(overlay);
            } else {
                entity.getViewComponent().removeChild(overlay);
            }

            beepTimer.capture();
        }
    }

    public void explode() {
        getGameWorld().getEntitiesInRange(entity.getBoundingBoxComponent().range(150, 150))
                .stream()
                .filter(e -> e.hasComponent(HealthIntComponent.class))
                .forEach(e -> FXGL.<GeoWarsApp>getAppCast().killEnemy(e));

        getGameWorld().getSingleton(GeoWarsType.GRID).getComponent(GridComponent.class)
                .applyExplosiveForce(2500, entity.getCenter(), 150);
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
