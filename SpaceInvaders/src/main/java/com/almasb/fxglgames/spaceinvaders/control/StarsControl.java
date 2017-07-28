package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.ViewComponent;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class StarsControl extends Control {

    private ViewComponent view;
    private List<Rectangle> stars;

    @Override
    public void onAdded(Entity entity) {
        stars = ((Group) view.getView().getNodes().get(0))
                .getChildren()
                .stream()
                .map(n -> (Rectangle) n)
                .collect(Collectors.toList());

        stars.forEach(this::respawn);

        // we only do this once when the stars are added
        stars.forEach(star -> star.setTranslateY(FXGLMath.random(10, FXGL.getAppHeight())));
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        for (int i = 0; i < stars.size(); i++) {
            Rectangle star = stars.get(i);
            star.setTranslateY(star.getTranslateY() + tpf * 100);

            if (star.getTranslateY() > FXGL.getAppHeight())
                respawn(star);
        }
    }

    private void respawn(Rectangle star) {
        star.setWidth(FXGLMath.random(0.5f, 2.5f));
        star.setHeight(FXGLMath.random(0.5f, 2.5f));
        star.setArcWidth(FXGLMath.random(0.5f, 2.5f));
        star.setArcHeight(FXGLMath.random(0.5f, 2.5f));

        star.setFill(Color.color(
                FXGLMath.randomTriangular(0.75f, 1.0f, 0.85f),
                FXGLMath.randomTriangular(0.75f, 1.0f, 0.85f),
                FXGLMath.randomTriangular(0.75f, 1.0f, 0.85f),
                FXGLMath.random(0.5f, 1.0f)
        ));

        star.setTranslateX(FXGLMath.random(0, FXGL.getAppWidth()));
        star.setTranslateY(-FXGLMath.random(10, FXGL.getAppHeight()));
    }
}
