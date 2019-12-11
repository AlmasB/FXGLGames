package com.almasb.fxglgames.tanks.actions;

import com.almasb.fxgl.ai.goap.GoapComponent;
import com.almasb.fxgl.ai.goap.WorldState;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.tanks.BattleTanksType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GoalSelectorComponent extends Component {

    private WorldState GUARD = new WorldState();
    private WorldState ATTACK = new WorldState();

    private ActionComponent actionComponent;
    private GoapComponent goap;

    public GoalSelectorComponent() {
        GUARD.add("guard", true);
        ATTACK.add("playerAlive", false);
    }

    @Override
    public void onAdded() {
        goap.setGoal(GUARD);
    }

    @Override
    public void onUpdate(double tpf) {
        Entity player = FXGL.getGameWorld().getSingleton(BattleTanksType.PLAYER);

        if (entity.distance(player) < 250) {
            if (goap.getGoal() != ATTACK) {
                goap.setGoal(ATTACK);

                actionComponent.clearActions();
                goap.updatePlan();
            }
        } else {
            if (goap.getGoal() != GUARD) {
                goap.setGoal(GUARD);

                actionComponent.clearActions();
                goap.updatePlan();
            }
        }
    }
}
