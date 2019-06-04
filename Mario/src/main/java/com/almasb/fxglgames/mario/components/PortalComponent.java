package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PortalComponent extends Component {

    private static final double PORTAL_COOLDOWN_SECONDS = 2.0;
    private LocalTimer timer;

    @Override
    public void onAdded() {
        timer = FXGL.newLocalTimer();
    }

    public void activate() {
        timer.capture();
    }

    public boolean isActive() {
        return timer.elapsed(Duration.seconds(PORTAL_COOLDOWN_SECONDS));
    }
}
