package com.almasb.fxglgames.mario;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.TimerAction;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TimeoutBoxComponent extends Component {

    private TimerAction timerAction;

    private IntegerProperty tick;

    public TimeoutBoxComponent(Text text, int duration) {
        tick = new SimpleIntegerProperty(duration);
        text.textProperty().bind(tick.asString());
    }

    private void tick() {
        tick.setValue(tick.getValue() - 1);

        if (tick.getValue() == 0) {
            entity.removeFromWorld();
        }
    }

    @Override
    public void onRemoved() {
        if (timerAction != null)
            timerAction.expire();
    }

    public void startCountdown() {
        tick();
        timerAction = FXGL.getGameTimer().runAtInterval(this::tick, Duration.seconds(1));
    }
}
