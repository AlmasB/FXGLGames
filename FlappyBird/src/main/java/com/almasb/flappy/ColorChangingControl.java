package com.almasb.flappy;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ColorChangingControl extends AbstractControl {

    private Rectangle view;
    private LocalTimer timer;
    private Duration interval = Duration.seconds(5);

    @Override
    public void onAdded(Entity entity) {
        view = (Rectangle) Entities.getMainView(entity).getView().getNodes().get(0);
        timer = FXGL.newLocalTimer();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (timer.elapsed(interval)) {
            view.setFill(view.getFill() == Color.BLACK ? Color.WHITE : Color.BLACK);
            timer.capture();
        }
    }
}
