package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.control.AStarMoveControl;
import com.almasb.fxglgames.pacman.control.MoveControl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GuardCoinAction extends GoalAction {

    private Entity targetCoin;

    @Override
    public void start() {
        if (targetCoin != null && targetCoin.isActive()) {
            getEntity().getControl(AStarMoveControl.class).moveTo(targetCoin.getPosition());
            return;
        }

        targetCoin = getEntity().getWorld()
                .getRandom(PacmanType.COIN)
                .orElse(null);

        if (targetCoin != null) {
            getEntity().getControl(AStarMoveControl.class).moveTo(targetCoin.getPosition());
        }
    }

    @Override
    public boolean reachedGoal() {
        return targetCoin == null || getEntity().getControl(AStarMoveControl.class).isDone();
    }

    @Override
    public void onUpdate(double tpf) {

    }
}
