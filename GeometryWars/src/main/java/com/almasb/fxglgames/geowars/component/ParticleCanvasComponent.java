package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.components.AccumulatedUpdateComponent;
import com.almasb.fxgl.entity.EntityGroup;
import com.almasb.fxglgames.geowars.GeoWarsType;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleCanvasComponent extends AccumulatedUpdateComponent {

    private GraphicsContext g;
    private EntityGroup particles;

    public ParticleCanvasComponent() {
        super(0);
    }

    @Override
    public void onAdded() {
        particles = getGameWorld().getGroup(GeoWarsType.EXPLOSION);

        Canvas canvas = new Canvas(getAppWidth(), getAppHeight());
        g = canvas.getGraphicsContext2D();

        entity.getViewComponent().addChild(canvas);
    }

    @Override
    public void onAccumulatedUpdate(double tpfSum) {
        g.clearRect(0, 0, getAppWidth(), getAppHeight());

        particles.forEach(explosion -> {
            explosion.getComponentOptional(ExplosionParticleComponent.class).ifPresent(comp -> {
                comp.onBackgroundUpdate(tpfSum, g);
            });
        });
    }
}
