package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CatapultLineIndicatorComponent extends Component {

    @Override
    public void onUpdate(double tpf) {
        Point2D vector = FXGL.getInput().getVectorToMouse(entity.getPosition());


        entity.rotateToVector(vector);
    }
}
