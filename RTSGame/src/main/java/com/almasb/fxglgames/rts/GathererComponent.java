package com.almasb.fxglgames.rts;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.state.EntityState;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * An example of a gatherer AI.
 * Can move to a resource to gather it.
 * Once the backpack is full, will move to nearest stockpile to deposit resources.
 * Then, will continue gathering resources.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(StateComponent.class)
public class GathererComponent extends Component {

    private StateComponent stateComponent;

    /**
     * Current target of this entity.
     * Can be null to indicate that there is no target.
     */
    private Entity target;

    /**
     * Position of the current target.
     * If no target present, then position to where the entity is moving.
     */
    private Point2D targetPosition;

    /**
     * Previous target of this entity.
     */
    private Entity prevTarget;

    private EntityState GATHERING = new EntityState() {
        private LocalTimer gatherTimer = FXGL.newLocalTimer();
        private ResourceComponent resourceComponent;

        @Override
        public void onEnteredFrom(EntityState entityState) {
            resourceComponent = target.getComponent(ResourceComponent.class);
            gatherTimer.capture();
        }

        @Override
        public void onUpdate(double tpf) {
            if (isBackpackFull()) {
                FXGL.getGameWorld()
                        .getClosestEntity(entity, e -> e.isType(EntityType.STOCKPILE))
                        .ifPresent(s -> sendTo(s));
                return;
            }

            if (resourceComponent.isEmpty()) {
                FXGL.getGameWorld()
                        // and matches resource type ...
                        .getClosestEntity(entity, e -> e.hasComponent(ResourceComponent.class) && !e.getComponent(ResourceComponent.class).isEmpty())
                        .ifPresent(resource -> sendTo(resource));
                return;
            }

            if (gatherTimer.elapsed(Duration.seconds(2))) {
                resourceComponent.gather();
                entity.getProperties().increment(resourceComponent.getType().toString(), +1);

                gatherTimer.capture();
            }
        }
    };

    private EntityState MOVING = new EntityState() {
        @Override
        protected void onUpdate(double tpf) {
            // reached destination, so stop moving
            if (entity.getPosition().distance(targetPosition) < 5) {

                if (target != null) {

                    if (target.hasComponent(ResourceComponent.class)) {
                        // target is a resource
                        stateComponent.changeState(GATHERING);

                    } else if (target.isType(EntityType.STOCKPILE)) {
                        // target is a stockpile
                        depositResources(target);

                        if (prevTarget != null) {
                            sendTo(prevTarget);
                        } else {
                            // no target, just be idle
                            stateComponent.changeStateToIdle();
                        }
                    }

                } else {
                    // no target, just be idle
                    stateComponent.changeStateToIdle();
                }

                return;
            }

            entity.translateTowards(targetPosition, 5);
        }
    };

    private boolean isBackpackFull() {
        return entity.getInt(ResourceType.WOOD.toString()) + entity.getInt(ResourceType.STONE.toString()) == 10;
    }

    private void depositResources(Entity stockpile) {
        for (var resourceType : ResourceType.values()) {
            int qty = entity.getInt(resourceType.toString());

            stockpile.getProperties().increment(resourceType.toString(), qty);

            entity.setProperty(resourceType.toString(), 0);
        }
    }

    public void sendTo(Entity target) {
        this.prevTarget = this.target;
        this.target = target;
        sendTo(target.getPosition());
    }

    public void sendTo(Point2D point) {
        targetPosition = point;

        stateComponent.changeState(MOVING);
    }
}
