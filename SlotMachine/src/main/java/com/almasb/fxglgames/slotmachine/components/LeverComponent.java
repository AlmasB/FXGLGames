package com.almasb.fxglgames.slotmachine.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxglgames.slotmachine.SlotMachineApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LeverComponent extends Component {

    private ViewComponent view;
    private String currentTexture = "lever0.png";

    @Override
    public void onAdded() {
        view.getView().setOnMouseClicked(e -> {
            trigger();
        });
    }

    public void trigger() {
        if (FXGL.<SlotMachineApp>getAppCast().isMachineSpinning())
            return;

        currentTexture = currentTexture.equals("lever0.png") ? "lever1.png" : "lever0.png";

        view.setTexture(currentTexture);

        FXGL.<SlotMachineApp>getAppCast().spin();
    }
}
