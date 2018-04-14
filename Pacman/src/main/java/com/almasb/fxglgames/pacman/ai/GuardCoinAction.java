package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.control.AStarMoveControl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GuardCoinAction extends GoalAction {

    private Entity targetCoin;

    @Override
    public void start() {
        if (targetCoin != null && targetCoin.isActive()) {
            getEntity().getComponent(AStarMoveControl.class).moveTo(targetCoin.getPosition());
            return;
        }

        targetCoin = getEntity().getWorld()
                .getRandom(PacmanType.COIN)
                .orElse(null);

        if (targetCoin != null) {
            getEntity().getComponent(AStarMoveControl.class).moveTo(targetCoin.getPosition());
        }
    }

    @Override
    public boolean reachedGoal() {
        return targetCoin == null || getEntity().getComponent(AStarMoveControl.class).isDone();
    }

    @Override
    public void onUpdate(double tpf) {

    }
}
