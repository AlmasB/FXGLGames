package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.ai.btree.annotation.TaskAttribute;
import com.almasb.fxgl.ai.utils.random.ConstantIntegerDistribution;
import com.almasb.fxgl.ai.utils.random.IntegerDistribution;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.control.MoveControl;
import com.almasb.fxglgames.pacman.control.MoveDirection;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class InversePlayerMoveAction extends GoalAction {

    @TaskAttribute
    public IntegerDistribution times = ConstantIntegerDistribution.ONE;

    private int t;

    private LocalTimer timer = FXGL.newLocalTimer();

    @Override
    public void start() {
        super.start();
        t = times.nextInt();
        timer.capture();
    }

    @Override
    public void reset() {
        times = ConstantIntegerDistribution.ONE;
        t = 0;
        super.reset();
    }

    @Override
    public boolean reachedGoal() {
        return timer.elapsed(Duration.seconds(t));
    }

    @Override
    public void onUpdate(double tpf) {
        MoveDirection moveDir = FXGL.<PacmanApp>getAppCast().getPlayerControl().getMoveDirection();

        getEntity().getControl(MoveControl.class).setMoveDirection(moveDir.next().next());
    }
}
