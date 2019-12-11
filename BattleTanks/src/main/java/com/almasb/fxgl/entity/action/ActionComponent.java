/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

import com.almasb.fxgl.entity.component.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ActionComponent extends Component {

    private static final Action IDLE = new IdleAction();

    private ObservableList<Action> actions = FXCollections.observableArrayList();

    private Action currentAction = IDLE;

    @Override
    public void onUpdate(double tpf) {
        if (currentAction.isCancelled()) {
            clearActions();
        }

        if (currentAction.isComplete()) {
            removeCurrentAction();
        } else {
            currentAction.onUpdate(tpf);
        }
    }

    @Override
    public void onRemoved() {
        clearActions();
    }

    public ObservableList<Action> actionsProperty() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    /**
     * @return true when not performing any actions
     */
    public boolean isIdle() {
        return currentAction == IDLE;
    }

    /**
     * @return true if there are more actions in the queue
     */
    public boolean hasNextActions() {
        return !actions.isEmpty();
    }

    /**
     * Clears all running and pending actions.
     */
    public void clearActions() {
        actions.forEach(Action::reset);
        actions.clear();
        removeCurrentAction();
    }

    /**
     * Add an action for this entity to execute.
     * If an entity is already executing an action,
     * this action will be queued.
     *
     * @param action next action to execute
     */
    public void addAction(Action action) {
        action.setEntity(entity);
        actions.add(action);
    }

    /**
     * Remove current executing action.
     * If there are more actions pending, the first pending action becomes current.
     */
    public void removeCurrentAction() {
        currentAction.reset();

        if (!isIdle() && !actions.isEmpty())
            actions.remove(0);

        currentAction = getNextAction();
    }

    public Action getNextAction() {
        return actions.size() > 0 ? actions.get(0) : IDLE;
    }
}
