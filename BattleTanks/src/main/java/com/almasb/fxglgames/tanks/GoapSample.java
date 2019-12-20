/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.ai.AIDebugViewComponent;
import com.almasb.fxgl.ai.goap.GoapAction;
import com.almasb.fxgl.ai.goap.GoapComponent;
import com.almasb.fxgl.ai.goap.MoveGoapAction;
import com.almasb.fxgl.ai.goap.WorldState;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.action.ContinuousAction;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.tanks.GoapSample.Type.*;

/**
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
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, "move", () -> {
            player.getComponent(ActionComponent.class).pushAction(new ContinuousAction() {
                Point2D p = new Point2D(getInput().getMouseXWorld(), getInput().getMouseYWorld());

                @Override
                protected void perform(double tpf) {

                    if (player.getPosition().distance(p) > 10) {
                        player.getTransformComponent().translateTowards(p, 5);
                    } else {
                        isCompleted = true;
                    }
                }
            });
        });
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

        // addPrecondition("closeTo" + player.getType(), true);

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
                .with(new ActionComponent())
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
                .with(new ActionComponent())
                .buildAndAttach();

        guard = entityBuilder()
                .at(600, 50)
                .type(GUARD)
                .view(new Text("GUARD"))
                //.with(new ActionComponent())
                .buildAndAttach();
    }

    private Set<GoapAction> initActions() {
        Set<GoapAction> actions = new LinkedHashSet<>();
        actions.add(new PickupWeapon());
        actions.add(new KillPlayer());
        actions.add(new PickupCoin());
//        actions.add(new ReviveAction());
//        actions.add(new BlowUpAction());
//        actions.add(new WaitAction());
        return actions;
    }

    private static class KillPlayer extends MoveGoapAction {
        public KillPlayer() {
            addPrecondition("playerInvincible", false);
            addPrecondition("playerAlive", true);
            addPrecondition("hasWeapon", true);

            addEffect("playerAlive", false);

            setTarget(player);
        }

        @Override
        public float getCost() {
            return (float) this.entity.distance(getTarget());
        }

        @Override
        public boolean isInRange() {
            return (this.entity.distance(getTarget()) < 5);
        }

        @Override
        public void move() {
            if (getEntity().distance(getTarget()) > 5) {
                getEntity().translate(getTarget().getPosition().subtract(getEntity().getPosition()).normalize().multiply(5));
            }
        }

        @Override
        public boolean isComplete() {
            return !getb("playerAlive");
        }

        @Override
        public void perform() {
            getGameState().setValue("playerAlive", false);
        }
    }

    private static class PickupWeapon extends MoveGoapAction {
        public PickupWeapon() {
            addPrecondition("hasWeapon", false);

            addEffect("hasWeapon", true);

            setTarget(weapon);
        }

        @Override
        public float getCost() {
            return (float) this.entity.distance(getTarget());
        }

        @Override
        public boolean isInRange() {
            return (this.entity.distance(getTarget()) < 5);
        }

        @Override
        public void move() {
            if (getEntity().distance(getTarget()) > 5) {
                getEntity().translate(getTarget().getPosition().subtract(getEntity().getPosition()).normalize().multiply(5));
            }
        }

        @Override
        public boolean isComplete() {
            return agent.getBoolean("hasWeapon");
        }

        @Override
        public void perform() {
            agent.setProperty("hasWeapon", true);
        }
    }

    private static class PickupCoin extends MoveGoapAction {
        public PickupCoin() {
            addPrecondition("hasCoin", false);

            addEffect("hasCoin", true);
            addEffect("playerInvincible", false);

            setTarget(coin);
        }

        @Override
        public float getCost() {
            return (float) this.entity.distance(getTarget());
        }

        @Override
        public boolean isInRange() {
            return (this.entity.distance(getTarget()) < 5);
        }

        @Override
        public void move() {
            if (getEntity().distance(getTarget()) > 5) {
                getEntity().translate(getTarget().getPosition().subtract(getEntity().getPosition()).normalize().multiply(5));
            }
        }

        @Override
        public boolean isComplete() {
            return agent.getBoolean("hasCoin");
        }

        @Override
        public void perform() {
            agent.setProperty("hasCoin", true);
            getGameState().setValue("playerInvincible", false);
        }
    }

//    private static class ReviveAction extends BaseGoapAction {
//        public ReviveAction() {
//            addPrecondition("playerAlive", false);
//
//            addEffect("playerAlive", true);
//
//            setTarget(player);
//        }
//    }
//
//    private static class BlowUpAction extends BaseGoapAction {
//        public BlowUpAction() {
//            setCost(10.0f);
//        }
//    }
//
//    private static class WaitAction extends BaseGoapAction {
//        public WaitAction() {
//            //addPrecondition("wait", false);
//
//            addEffect("wait", true);
//        }
//
//        @Override
//        public boolean perform() {
//            done = false;
//
//            //System.out.println(this.entity.getType() + ": Performing wait");
//
//            this.entity.setProperty("wait", true);
//
//            return true;
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
