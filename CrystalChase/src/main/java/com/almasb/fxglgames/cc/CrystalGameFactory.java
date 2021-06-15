package com.almasb.fxglgames.cc;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.scene3d.Cone;
import com.almasb.fxgl.scene3d.Prism;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalGameFactory implements EntityFactory {

    @Spawns("crystal")
    public Entity newCrystal(SpawnData data) {
        var view = new Cone();
        view.setBottomRadius(1);
        view.setTopRadius(0.2);
        view.setHeight(1);

        var e = entityBuilder(data)
                .type(EntityType.CRYSTAL)
                .bbox(BoundingShape.box3D(2, 1, 2))
                .view(view)
                .collidable()
                .with(new ProjectileComponent(new Point2D(0, 1), 1.5).allowRotation(false))
                .with(new NetworkComponent())
                .build();

        e.setScaleUniform(0);

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .scale(e)
                .from(new Point3D(0, 0, 0))
                .to(new Point3D(1, 1, 1))
                .buildAndPlay();

        return e;
    }

    @Spawns("ground")
    public Entity newGround(SpawnData data) {
        var mat = new PhongMaterial(Color.BROWN);

        var view = new Box(20, 0.4, 20);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.GROUND)
                .bbox(BoundingShape.box3D(20, 0.4, 20))
                .view(view)
                .collidable()
                .with(new NetworkComponent())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        var mat = new PhongMaterial(Color.BLUE);

        var view = new Box(2, 0.2, 2);
        view.setMaterial(mat);

        return entityBuilder(data)
                .type(EntityType.PLATFORM)
                .bbox(BoundingShape.box3D(2, 0.2, 2))
                .view(view)
                .collidable()
                .with(new NetworkComponent())
                .build();
    }
}
