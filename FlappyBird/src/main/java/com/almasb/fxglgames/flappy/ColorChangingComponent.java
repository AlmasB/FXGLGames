package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ColorChangingComponent extends Component {

    private Rectangle view;
    private LocalTimer timer;
    private Duration interval = Duration.seconds(5);

    @Override
    public void onAdded() {
        view = entity.getObject("rect");
        timer = newLocalTimer();
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(interval)) {

            Color nextViewColor = ((Color)view.getFill()).invert();
            Color stageColor = nextViewColor.invert();

            view.setFill(nextViewColor);
            set("stageColor", stageColor);

            timer.capture();
        }
    }
}
