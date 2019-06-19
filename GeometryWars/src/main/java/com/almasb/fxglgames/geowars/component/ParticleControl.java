package com.almasb.fxglgames.geowars.component;

import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleControl extends Component {
    @Override
    public void onUpdate(double tpf) {
        ProjectileComponent control = entity.getComponent(ProjectileComponent.class);
        control.setSpeed(control.getSpeed() * (1 - 3*tpf));
    }
}
