package com.almasb.fxglgames.tanks;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveComponent extends Component {

    private BoundingBoxComponent bbox;

    private MoveDirection moveDir;

    private boolean movedThisFrame = false;

    public void setMoveDirection(MoveDirection moveDir) {
        if (movedThisFrame)
            return;

        movedThisFrame = true;

        this.moveDir = moveDir;

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
    }

    @Override
    public void onAdded() {
        moveDir = FXGLMath.random(MoveDirection.values()).get();
    }

    private double speed = 0;

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * 60;

        movedThisFrame = false;
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
            blocks = FXGL.getGameWorld().getEntitiesByType(BattleTanksType.WALL);
        }

        velocity.set((float) dx, (float) dy);

        int length = Math.round(velocity.length());

        velocity.normalizeLocal();

        for (int i = 0; i < length; i++) {
            entity.translate(velocity.x, velocity.y);

            boolean collision = false;

            for (int j = 0; j < blocks.size(); j++) {
                if (blocks.get(j).getBoundingBoxComponent().isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                entity.translate(-velocity.x, -velocity.y);
                break;
            }
        }
    }
}
