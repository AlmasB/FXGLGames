package com.almasb.fxglgames.pacman.components.ai;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.pacman.PacmanType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(AStarMoveComponent.class)
public class DelayedChasePlayerComponent extends Component {

    private AStarMoveComponent astar;

    @Override
    public void onUpdate(double tpf) {
        if (astar.isPathEmpty() && !astar.isMoving()) {
            var player = FXGL.getGameWorld().getSingleton(PacmanType.PLAYER);

            int x = player.call("getCellX");
            int y = player.call("getCellY");

            astar.moveToCell(x, y);
        }
    }
}
