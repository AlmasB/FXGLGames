package com.almasb.fxglgames.tanks.components.ai;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxglgames.tanks.BattleTanksType;
import com.almasb.fxglgames.tanks.components.TankViewComponent;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.tanks.BattleTanksType.*;

public class ShootPlayerComponent extends Component {

    private Entity player;

    @Override
    public void onAdded() {
        player = getGameWorld().getSingleton(PLAYER);
    }

    @Override
    public void onUpdate(double tpf) {
        // TODO: generalize magic numbers
        int x = (int)((entity.getX() + 30/2 ) / 30);
        int y = (int)((entity.getY() + 30/2 ) / 30);

        int px = (int)((player.getX() + 30/2 ) / 30);
        int py = (int)((player.getY() + 30/2 ) / 30);

        if (x == px || y == py) {
            shoot();
        }
    }

    public void shoot() {
        spawn("Bullet", new SpawnData(getEntity().getCenter())
                .put("direction", player.getPosition().subtract(entity.getPosition()))
                .put("owner", entity)
        );
    }
}
