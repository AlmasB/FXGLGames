package com.almasb.fxglgames.tanks.actions;

import com.almasb.fxgl.ai.goap.MoveGoapAction;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.tanks.BattleTanksType;

import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class GuardAction extends MoveGoapAction {

    private LazyValue<Entity> flag = new LazyValue<>(
            () -> FXGL.getGameWorld().getSingleton(BattleTanksType.PLAYER_FLAG)
    );

    public GuardAction() {
        addEffect("guard", true);
    }

    @Override
    public float getCost() {
        return (float) entity.distance(flag.get());
    }

    @Override
    public boolean isInRange() {
        // TODO: generalize magic numbers
        int x = (int) ((entity.getX() + 30 / 2) / 30);
        int y = (int) ((entity.getY() + 30 / 2) / 30);

        int px = (int) ((flag.get().getX() + 30 / 2) / 30);
        int py = (int) ((flag.get().getY() + 30 / 2) / 30);

        return distance(x, y, px, py) < 6;
    }

    private int distance(int x0, int y0, int x, int y) {
        return abs(x0 - x) + abs(y0 - y);
    }

    @Override
    public void move() {
        int x = (int) ((entity.getX() + 30 / 2) / 30);
        int y = (int) ((entity.getY() + 30 / 2) / 30);

        int px = (int) ((flag.get().getX() + 30 / 2) / 30);
        int py = (int) ((flag.get().getY() + 30 / 2) / 30);

        AStarMoveComponent astar = entity.getComponent(AStarMoveComponent.class);

        if (!astar.isPathEmpty())
            return;

        var validCells = astar.getGrid()
                .getCells()
                .stream()
                .filter(c -> c.getState().isWalkable() && distance(c.getX(), c.getY(), px, py) < 6)
                .collect(Collectors.toList());

        FXGLMath.random(validCells)
                .ifPresent(c -> astar.moveToCell(c.getX(), c.getY()));
    }

    @Override
    public void perform() {
        //isCompleted = true;
    }
}
