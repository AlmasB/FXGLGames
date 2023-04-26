package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.geowars.GeoWarsApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrystalComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        rotate(tpf);
        followPlayer(tpf);
    }

    private void rotate(double tpf) {
        getEntity().rotateBy(180 * tpf);

        if (getEntity().getRotation() >= 360) {
            getEntity().setRotation(0);
        }
    }

    private void followPlayer(double tpf) {
        Entity player = FXGL.<GeoWarsApp>getAppCast().getPlayer();
        if (getEntity().distance(player) < 150) {
            getEntity().translateTowards(player.getCenter(), 150 * tpf);
        }
    }
}
