package com.almasb.fxglgames.rts;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(StateComponent.class)
public class GathererComponent extends Component {

    private StateComponent stateComponent;
    private LocalTimer gatherTimer = FXGL.newLocalTimer();
    private Entity resource;

    private EntityState GATHERING = new EntityState() {
        @Override
        public void onEnteredFrom(EntityState entityState) {
            gatherTimer.capture();
        }

        @Override
        public void onUpdate(double tpf) {
            if (gatherTimer.elapsed(Duration.seconds(2))) {
                var resourceComponent = resource.getComponent(ResourceComponent.class);

                resourceComponent.gather();
                entity.getProperties().increment(resourceComponent.getType().toString(), +1);

                gatherTimer.capture();
            }
        }
    };

    private EntityState MOVING = tpf -> {

    };

    public void startGathering(Entity resource) {
        this.resource = resource;

        stateComponent.changeState(GATHERING);
    }
}
