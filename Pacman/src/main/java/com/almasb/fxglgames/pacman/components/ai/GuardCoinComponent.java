package com.almasb.fxglgames.pacman.components.ai;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.pathfinding.Cell;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.components.CoinHighlightViewComponent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(AStarMoveComponent.class)
public class GuardCoinComponent extends Component {

    private AStarMoveComponent astar;

    private Entity targetCoin;

    @Override
    public void onUpdate(double tpf) {
        if (targetCoin == null || !targetCoin.isActive()) {
            targetCoin = entity.getWorld()
                    .getRandom(PacmanType.COIN)
                    .orElse(null);

            if (targetCoin != null)
                targetCoin.addComponent(new CoinHighlightViewComponent());

        } else {
            // TODO: seems commonly needed, maybe move to a method in astar?
            if (astar.isPathEmpty() && !astar.isMoving()) {
                int x = targetCoin.call("getCellX");
                int y = targetCoin.call("getCellY");
                Cell cell = astar.getGrid().get(x, y);

                astar.getGrid()
                        .getRandomCell(c -> c.getState().isWalkable() && c.distance(cell) < 5)
                        .ifPresent(astar::moveToCell);
            }
        }
    }
}
