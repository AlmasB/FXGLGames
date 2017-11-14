package com.almasb.fxglgames.pacman.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveControl extends Control {

    private PositionComponent position;
    private BoundingBoxComponent bbox;

    private MoveDirection moveDir;

    public void setMoveDirection(MoveDirection moveDir) {
        this.moveDir = moveDir;
    }

    @Override
    public void onAdded(Entity entity) {
        moveDir = FXGLMath.random(MoveDirection.values()).get();
    }

    private double speed = 0;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = tpf * 60;

        switch (moveDir) {
            case UP:
                up();
                break;

            case DOWN:
                down();
                break;

            case LEFT:
                left();
                break;

            case RIGHT:
                right();
                break;
        }

        if (position.getX() < 0) {
            position.setX(PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE - bbox.getWidth() - 5);
        }

        if (bbox.getMaxXWorld() > PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE) {
            position.setX(0);
        }
    }

    private void up() {
        move(0, -5 * speed);
    }

    private void down() {
        move(0, 5 * speed);
    }

    private void left() {
        move(-5 * speed, 0);
    }

    private void right() {
        move(5 * speed, 0);
    }

    private List<Entity> blocks;
    private Vec2 velocity = new Vec2();

    private void move(double dx, double dy) {
        if (!getEntity().isActive())
            return;

        if (blocks == null) {
            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.BLOCK);
        }

        velocity.set((float) dx, (float) dy);

        int length = FXGLMath.roundPositive(velocity.length());

        velocity.normalizeLocal();

        for (int i = 0; i < length; i++) {
            position.translate(velocity.x, velocity.y);

            boolean collision = false;

            for (int j = 0; j < blocks.size(); j++) {
                if (blocks.get(j).getBoundingBoxComponent().isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                position.translate(-velocity.x, -velocity.y);
                break;
            }
        }
    }
}
