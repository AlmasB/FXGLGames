/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.ai.goap.GoapAction;
import com.almasb.fxgl.ai.goap.GoapAgent;
import com.almasb.fxgl.ai.goap.GoapComponent;
import com.almasb.fxgl.ai.goap.WorldState;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GoapSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("GoapSample");
    }

    private static Entity player, coin, weapon, agent, guard;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerInvincible", true);
        vars.put("playerAlive", true);
    }

    @Override
    protected void initGame() {

        initObjects();
        HashSet<GoapAction> actions = initActions();

        GoapComponent GoapComponent = new GoapComponent(new KillerAgent(), 100, actions);
        agent.addComponent(GoapComponent);

        guard.addComponent(new GoapComponent(new GuardAgent(), 125, actions));
    }

    private void initObjects() {
        player = entityBuilder()
                .at(300, 300)
                .view(new Text("PLAYER"))
                .buildAndAttach();

        coin = entityBuilder()
                .at(500, 100)
                .view(new Text("COIN"))
                .buildAndAttach();

        weapon = entityBuilder()
                .at(30, 500)
                .view(new Text("WEAPON"))
                .buildAndAttach();

        agent = entityBuilder()
                .at(400, 400)
                .view(new Text("AGENT"))
                .buildAndAttach();

        guard = entityBuilder()
                .at(600, 50)
                .view(new Text("GUARD"))
                .buildAndAttach();

        agent.setProperty("hasWeapon", false);
        agent.setProperty("hasCoin", false);
    }

    private HashSet<GoapAction> initActions() {
        HashSet<GoapAction> actions = new HashSet<>();
        actions.add(new PickupWeapon());
        actions.add(new KillPlayer());
        actions.add(new PickupCoin());
        actions.add(new ReviveAction());
        actions.add(new BlowUpAction());
        actions.add(new WaitAction());
        return actions;
    }

    private class KillerAgent implements GoapAgent {

        @Override
        public WorldState obtainWorldState(Entity entity) {
            WorldState worldState = new WorldState();
            worldState.add("playerInvincible", getGameState().getBoolean("playerInvincible"));
            worldState.add("playerAlive", getGameState().getBoolean("playerAlive"));
            worldState.add("hasWeapon", entity.getBoolean("hasWeapon"));
            worldState.add("hasCoin", entity.getBoolean("hasCoin"));

            return worldState;
        }

        @Override
        public WorldState createGoalState(Entity entity) {
            WorldState goal = new WorldState();
            goal.add("playerAlive", false);
            return goal;
        }

        @Override
        public void planFailed(Entity entity, WorldState failedGoal) {}

        @Override
        public void planFound(Entity entity, WorldState goal, Queue<GoapAction> actions) {
            System.out.println("Plan found!");
            for (GoapAction action : actions) {
                System.out.println(action);
            }
        }

        @Override
        public void actionsFinished(Entity entity) {
            //entity.removeComponent(GoapComponent.class);
        }

        @Override
        public void planAborted(Entity entity, GoapAction aborter) {}
    }

    private class GuardAgent implements GoapAgent {

        @Override
        public WorldState obtainWorldState(Entity entity) {
            WorldState worldState = new WorldState();
            worldState.add("playerInvincible", getGameState().getBoolean("playerInvincible"));
            worldState.add("playerAlive", getGameState().getBoolean("playerAlive"));

            return worldState;
        }

        @Override
        public WorldState createGoalState(Entity entity) {
            WorldState goal = new WorldState();

            if (!getGameState().getBoolean("playerAlive")) {
                goal.add("playerAlive", true);
            } else {
                goal.add("wait", true);
            }

            return goal;
        }

        @Override
        public void planFailed(Entity entity, WorldState failedGoal) {}

        @Override
        public void planFound(Entity entity, WorldState goal, Queue<GoapAction> actions) {
            System.out.println("Plan found! Guard");
            for (GoapAction action : actions) {
                System.out.println(action);
            }
        }

        @Override
        public void actionsFinished(Entity entity) {
            //entity.removeComponent(GoapComponent.class);
        }

        @Override
        public void planAborted(Entity entity, GoapAction aborter) {}
    }

    private static class BaseGoapAction extends GoapAction {

        private boolean done = false;

        @Override
        public void reset() {
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public boolean perform() {
            done = true;
            // pickup / kill instantly
            return true;
        }

        @Override
        public boolean requiresInRange() {
            return true;
        }
    }

    private static class KillPlayer extends BaseGoapAction {
        public KillPlayer() {
            addPrecondition("playerInvincible", false);
            addPrecondition("playerAlive", true);
            addPrecondition("hasWeapon", true);
            addEffect("playerAlive", false);

            setTarget(player);
        }

        @Override
        public boolean perform() {
            getGameState().setValue("playerAlive", false);
            return super.perform();
        }
    }

    private static class PickupWeapon extends BaseGoapAction {
        public PickupWeapon() {
            addPrecondition("hasWeapon", false);
            addEffect("hasWeapon", true);

            setTarget(weapon);
        }

        @Override
        public boolean perform() {
            agent.setProperty("hasWeapon", true);
            return super.perform();
        }
    }

    private static class PickupCoin extends BaseGoapAction {
        public PickupCoin() {
            addPrecondition("hasCoin", false);
            addEffect("hasCoin", true);
            addEffect("playerInvincible", false);

            setTarget(coin);
        }

        @Override
        public boolean perform() {
            agent.setProperty("hasCoin", true);
            getGameState().setValue("playerInvincible", false);
            return super.perform();
        }
    }

    private static class ReviveAction extends BaseGoapAction {
        public ReviveAction() {
            addPrecondition("playerAlive", false);
            addEffect("playerAlive", true);

            setTarget(player);
        }
    }

    private static class BlowUpAction extends BaseGoapAction {
        public BlowUpAction() {
            setCost(10.0f);
        }
    }

    private static class WaitAction extends BaseGoapAction {
        public WaitAction() {
            addEffect("wait", true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
