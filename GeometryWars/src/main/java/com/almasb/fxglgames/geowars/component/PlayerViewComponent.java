package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.components.view.ChildViewComponent;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;

import static com.almasb.fxgl.dsl.FXGL.getip;
import static com.almasb.fxglgames.geowars.Config.MAX_CHARGES_SECONDARY;
import static com.almasb.fxglgames.geowars.Config.PLAYER_HP;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerViewComponent extends ChildViewComponent {

    public PlayerViewComponent() {
        super(36, 36, false);

        var arcHP = new Arc(0, 0, 50, 50, -90, 0);
        arcHP.setStroke(Color.GREEN.brighter());
        arcHP.setStrokeWidth(2.5);
        arcHP.setFill(null);
        arcHP.lengthProperty().bind(
                getip("hp").multiply(-360.0).divide(PLAYER_HP)
        );

        var arcSP = new Arc(0, 0, 40, 40, -90, 0);
        arcSP.setStroke(Color.LIGHTBLUE.brighter().brighter());
        arcSP.setStrokeWidth(3.5);
        arcSP.setFill(null);
        arcSP.lengthProperty().bind(
                getip("secondaryCharge").multiply(-360.0).divide(MAX_CHARGES_SECONDARY)
        );
        arcSP.opacityProperty().bind(
                Bindings.when(getip("secondaryCharge").lessThan(MAX_CHARGES_SECONDARY))
                        .then(0.15)
                        .otherwise(1.0)
        );

        getViewRoot().getChildren().addAll(arcHP, arcSP);
    }
}
