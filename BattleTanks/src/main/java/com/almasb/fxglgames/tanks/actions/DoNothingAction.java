package com.almasb.fxglgames.tanks.actions;

import com.almasb.fxgl.ai.goap.MoveGoapAction;

public class DoNothingAction extends MoveGoapAction {

    public DoNothingAction() {
        addEffect("goal", true);
    }

    @Override
    public boolean isInRange() {
        return false;
    }

    @Override
    public void move() {
        //System.out.println("moving");
    }

    @Override
    public void perform() {
        //System.out.println("Performing");
    }
}
