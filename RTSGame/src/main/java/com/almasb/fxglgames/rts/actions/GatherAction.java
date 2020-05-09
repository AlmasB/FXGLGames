package com.almasb.fxglgames.rts.actions;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ContinuousAction;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.rts.ResourceComponent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GatherAction extends ContinuousAction {

    private LocalTimer gatherTimer = FXGL.newLocalTimer();
    private Entity resource;

    public GatherAction(Entity resource) {
        this.resource = resource;
    }

    @Override
    protected void onStarted() {
        gatherTimer.capture();
    }

    @Override
    protected void perform(double tpf) {
        if (entity.getInt("wood") == 10
                || resource.getComponent(ResourceComponent.class).isEmpty()) {

            setComplete();
            return;
        }

        if (gatherTimer.elapsed(Duration.seconds(2.0))) {
            resource.getComponent(ResourceComponent.class).gather();
            entity.getProperties().increment("wood", +1);
            gatherTimer.capture();
        }
    }
}
