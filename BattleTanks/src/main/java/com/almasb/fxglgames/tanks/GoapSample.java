/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.ai.AIDebugViewComponent;
import com.almasb.fxgl.ai.goap.GoapAction;
import com.almasb.fxgl.ai.goap.GoapListener;
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
import static com.almasb.fxglgames.tanks.GoapSample.Type.*;

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

    public enum Type {
        PLAYER, COIN, WEAPON, AGENT, GUARD
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerInvincible", true);
        vars.put("playerAlive", true);
    }

    @Override
    protected void initGame() {

        initObjects();

        WorldState goalKiller = new WorldState();
        goalKiller.add("playerAlive", false);

        WorldState goalGuard = new WorldState();
        goalGuard.add("wait", true);

//        if (!getGameState().getBoolean("playerAlive")) {
//            goalGuard.add("playerAlive", true);
//        } else {
//            goalGuard.add("wait", true);
//        }

        agent.addComponent(new GoapComponent(getGameState().getProperties(), goalKiller, initActions()));
        //guard.addComponent(new GoapComponent(getGameState().getProperties(), goalGuard, initActions()));

        agent.addComponent(new AIDebugViewComponent());
        //guard.addComponent(new AIDebugViewComponent());

        agent.setProperty("hasWeapon", false);
        agent.setProperty("hasCoin", false);

        guard.setProperty("wait", false);
    }

    @Override
    protected void onUpdate(double tpf) {
        agent.setProperty("closeTo" + coin.getType(), agent.distance(coin) < 10);
        agent.setProperty("closeTo" + weapon.getType(), agent.distance(weapon) < 10);
        agent.setProperty("closeTo" + player.getType(), agent.distance(player) < 10);
    }

    private void initObjects() {
        player = entityBuilder()
                .at(300, 300)
                .type(PLAYER)
                .view(new Text("PLAYER"))
                .buildAndAttach();

        coin = entityBuilder()
                .at(500, 100)
                .type(COIN)
                .view(new Text("COIN"))
                .buildAndAttach();

        weapon = entityBuilder()
                .at(30, 500)
                .type(WEAPON)
                .view(new Text("WEAPON"))
                .buildAndAttach();

        agent = entityBuilder()
                .at(400, 400)
                .type(AGENT)
                .view(new Text("AGENT"))
                .buildAndAttach();

        guard = entityBuilder()
                .at(600, 50)
                .type(GUARD)
                .view(new Text("GUARD"))
                .buildAndAttach();
    }

    private HashSet<GoapAction> initActions() {
        HashSet<GoapAction> actions = new HashSet<>();
        actions.add(new PickupWeapon());
        actions.add(new KillPlayer());
        actions.add(new PickupCoin());
        actions.add(new ReviveAction());
        actions.add(new BlowUpAction());
        actions.add(new WaitAction());
        actions.add(new MoveAction(player));
        actions.add(new MoveAction(coin));
        actions.add(new MoveAction(weapon));
        return actions;
    }

    private static class BaseGoapAction extends GoapAction {

        protected boolean done = false;

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
    }

    private static class MoveAction extends BaseGoapAction {

        public MoveAction(Entity target) {
            addPrecondition("closeTo" + target.getType(), false);

            addEffect("closeTo" + target.getType(), true);

            setTarget(target);
        }

        @Override
        public boolean perform() {
            if (getEntity().distance(getTarget()) > 5) {
                getEntity().translate(getTarget().getPosition().subtract(getEntity().getPosition()).normalize().multiply(5));
            } else {
                done = true;
                this.entity.setProperty("closeTo" + getTarget().getType(), true);
            }

            return true;
        }
    }

    private static class KillPlayer extends BaseGoapAction {
        public KillPlayer() {
            addPrecondition("playerInvincible", false);
            addPrecondition("playerAlive", true);
            addPrecondition("closeTo" + player.getType(), true);
            addPrecondition("hasWeapon", true);

            addEffect("playerAlive", false);

            setTarget(player);
        }

        @Override
        public boolean perform() {
            if (this.entity.distance(getTarget()) > 10)
                return false;

            getGameState().setValue("playerAlive", false);
            return super.perform();
        }
    }

    private static class PickupWeapon extends BaseGoapAction {
        public PickupWeapon() {
            addPrecondition("hasWeapon", false);
            addPrecondition("closeTo" + weapon.getType(), true);

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
            addPrecondition("closeTo" + coin.getType(), true);

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
            addPrecondition("wait", false);

            addEffect("wait", true);
        }

        @Override
        public boolean perform() {
            done = false;

            //System.out.println(this.entity.getType() + ": Performing wait");

            this.entity.setProperty("wait", true);

            return true;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
