package com.almasb.slotmachine.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.slotmachine.SlotMachineApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LeverControl extends AbstractControl {

    private ViewComponent view;
    private String currentTexture = "lever0.png";

    @Override
    public void onAdded(Entity entity) {
        view = Entities.getView(entity);

        view.getView().setOnMouseClicked(e -> {
            trigger();
        });
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void trigger() {
        if (FXGL.<SlotMachineApp>getAppCast().isMachineSpinning())
            return;

        currentTexture = currentTexture.equals("lever0.png") ? "lever1.png" : "lever0.png";

        view.setTexture(currentTexture);

        FXGL.<SlotMachineApp>getAppCast().spin();
    }
}
