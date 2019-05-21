package com.almasb.fxglgames.spacerunner.ai;

import com.almasb.fxgl.dsl.components.AccumulatedUpdateComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spacerunner.components.MoveComponent;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SquadAIComponent extends AccumulatedUpdateComponent {

    private MoveComponent move;
    private Entity currentBestPoint = null;

    public SquadAIComponent() {
        super(5);
    }

    @Override
    public void onAccumulatedUpdate(double tpfSum) {
        if (move.isMoving())
            return;

        Entity point = SquadAI.INSTANCE.getBestPoint(entity);

        if (point == currentBestPoint)
            return;

        if (currentBestPoint != null) {
            currentBestPoint.getComponent(AIPointComponent.class).setOccupiedBy(null);
        }

        currentBestPoint = point;
        currentBestPoint.getComponent(AIPointComponent.class).setOccupiedBy(entity);

        if (entity.distance(point) > 30) {
            move.moveTo(point, 100);
        }
    }
}
