package com.almasb.fxglgames.rts;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.rts.state.EntityState;
import com.almasb.fxglgames.rts.state.StateComponent;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(StateComponent.class)
public class GathererComponent extends Component {

    private StateComponent stateComponent;
    private LocalTimer gatherTimer = FXGL.newLocalTimer();

    private Entity resource;
    private Entity stockpile;
    private Point2D point;

    private EntityState GATHERING = new EntityState() {
        private ResourceComponent resourceComponent;

        @Override
        public void onEnteredFrom(EntityState entityState) {
            gatherTimer.capture();
            resourceComponent = resource.getComponent(ResourceComponent.class);
        }

        @Override
        public void onUpdate(double tpf) {
            // TODO: check if resource qty is available, i.e. not empty

            int qtyCarrying = entity.getInt(resourceComponent.getType().toString());

            if (qtyCarrying == 10) {
                FXGL.getGameWorld()
                        .getClosestEntity(entity, e -> e.isType(EntityType.STOCKPILE))
                        .ifPresent(s -> sendToStockpile(s));
                return;
            }

            if (gatherTimer.elapsed(Duration.seconds(2))) {
                resourceComponent.gather();
                entity.getProperties().increment(resourceComponent.getType().toString(), +1);

                gatherTimer.capture();
            }
        }

        @Override
        public void onExitingTo(EntityState entityState) {
            resource = null;
        }
    };

    private EntityState MOVING = tpf -> {

        // reached destination
        if (entity.getPosition().distance(point) < 5) {
            if (resource != null) {
                startGathering(resource);
            } else if (stockpile != null) {
                depositResources(stockpile);
                stateComponent.changeStateToIdle();
            } else {
                stateComponent.changeStateToIdle();
            }

            return;
        }

        entity.translateTowards(point, 5);
    };

    private void depositResources(Entity stockpile) {
        for (var resourceType : ResourceType.values()) {
            int qty = entity.getInt(resourceType.toString());

            stockpile.getProperties().increment(resourceType.toString(), qty);

            entity.setProperty(resourceType.toString(), 0);
        }
    }

    private void startGathering(Entity resource) {
        this.resource = resource;

        stateComponent.changeState(GATHERING);
    }

    public void startMoving(Point2D point) {
        this.point = point;

        stateComponent.changeState(MOVING);
    }

    public void sendToGather(Entity resource) {
        this.point = resource.getPosition();

        stateComponent.changeState(MOVING);

        this.resource = resource;
    }

    public void sendToStockpile(Entity stockpile) {
        this.point = stockpile.getPosition();

        stateComponent.changeState(MOVING);

        this.stockpile = stockpile;
    }
}
