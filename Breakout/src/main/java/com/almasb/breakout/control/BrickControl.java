package com.almasb.breakout.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.MainViewComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BrickControl extends AbstractControl {

    private int lives = 2;

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }

    public void onHit() {
        FXGL.getAudioPlayer().playSound("brick_hit.wav");

        lives--;

        if (lives == 1) {
            MainViewComponent view = getEntity().getComponentUnsafe(MainViewComponent.class);
            view.setView(FXGL.getAssetLoader().loadTexture("brick_blue_cracked.png", 232 / 3, 104 / 3));
        } else if (lives == 0) {
            getEntity().removeFromWorld();
        }
    }
}
