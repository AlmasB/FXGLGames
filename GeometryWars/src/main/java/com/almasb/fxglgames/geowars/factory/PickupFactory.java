package com.almasb.fxglgames.geowars.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxglgames.geowars.GeoWarsApp;
import com.almasb.fxglgames.geowars.component.PickupComponent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.Config.PICKUP_RICOCHET_DURATION;
import static com.almasb.fxglgames.geowars.Config.PICKUP_Z_INDEX;
import static com.almasb.fxglgames.geowars.GeoWarsType.PICKUP_CRYSTAL;
import static com.almasb.fxglgames.geowars.GeoWarsType.PICKUP_RICOCHET;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PickupFactory implements EntityFactory {

    @Spawns("Crystal")
    public Entity spawnCrystal(SpawnData data) {
        var name = "light_02.png";

        var w = 64;
        var h = 64;

        var t = texture("particles/" + name, w, h).multiplyColor(Color.YELLOW.brighter());
        t.setTranslateX(-(w / 2.0 - 32 / 2.0));
        t.setTranslateY(-(h / 2.0 - 32 / 2.0));
        if (!FXGL.isMobile()) {
            t.setEffect(new BoxBlur(15, 15, 3));
        }

        return entityBuilder(data)
                .type(PICKUP_CRYSTAL)
                .scale(0.25, 0.25)
                .view(t)
                .viewWithBBox(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(1)))
                .zIndex(100)
                .with(new CollidableComponent(true))
                .with(new PickupComponent(() -> inc("multiplier", +1)))
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .zIndex(PICKUP_Z_INDEX)
                .build();
    }

    @Spawns("PickupRicochet")
    public Entity spawnRicochet(SpawnData data) {
        var name = "light_02.png";

        var w = 64;
        var h = 64;

        var t = texture("particles/" + name, w, h).multiplyColor(Color.BLUE.brighter());
        t.setTranslateX(-(w / 2.0 - 32 / 2.0));
        t.setTranslateY(-(h / 2.0 - 32 / 2.0));
        if (!FXGL.isMobile()) {
            t.setEffect(new BoxBlur(15, 15, 3));
        }

        return entityBuilder(data)
                .type(PICKUP_RICOCHET)
                .scale(0.5, 0.5)
                .view(t)
                .viewWithBBox(texture("YellowCrystal.png").multiplyColor(Color.AQUA).toAnimatedTexture(8, Duration.seconds(1)))
                .zIndex(100)
                .with(new CollidableComponent(true))
                .with(new PickupComponent(() -> {
                    FXGL.<GeoWarsApp>getAppCast()
                            .getPlayer()
                            .getComponent(EffectComponent.class)
                            .startEffect(new RicochetEffect());
                }))
                .with(new ExpireCleanComponent(Duration.seconds(10)))
                .zIndex(PICKUP_Z_INDEX)
                .build();
    }
    
    private static class RicochetEffect extends Effect {

        public RicochetEffect() {
            super(PICKUP_RICOCHET_DURATION);
        }

        @Override
        public void onStart(Entity entity) {
            set("isRicochet", true);
        }

        @Override
        public void onEnd(Entity entity) {
            set("isRicochet", false);
        }
    }
}
