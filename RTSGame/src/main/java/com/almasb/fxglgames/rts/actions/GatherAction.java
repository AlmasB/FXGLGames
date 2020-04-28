package com.almasb.fxglgames.rts.actions;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ContinuousAction;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GatherAction extends ContinuousAction {

    private LocalTimer gatherTimer = FXGL.newLocalTimer();
    private Entity tree;

    public GatherAction(Entity tree) {
        this.tree = tree;
    }

    @Override
    protected void onStarted() {
        gatherTimer.capture();
    }

    @Override
    protected void perform(double tpf) {
        if (entity.getInt("wood") == 10) {
            setComplete();
            return;
        }

        if (gatherTimer.elapsed(Duration.seconds(2.0))) {
            entity.getProperties().increment("wood", +1);
            gatherTimer.capture();
        }
    }
}
