package com.almasb.fxglgames.tanks.components.ai;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.spawn;
import static com.almasb.fxglgames.tanks.BattleTanksType.PLAYER;

public class ShootPlayerComponent extends Component {

    private Entity player;

    @Override
    public void onAdded() {
        player = getGameWorld().getSingleton(PLAYER);
    }

    @Override
    public void onUpdate(double tpf) {
        // TODO: generalize magic numbers
        int x = (int) ((entity.getX() + 30 / 2) / 30);
        int y = (int) ((entity.getY() + 30 / 2) / 30);

        int px = (int) ((player.getX() + 30 / 2) / 30);
        int py = (int) ((player.getY() + 30 / 2) / 30);

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
