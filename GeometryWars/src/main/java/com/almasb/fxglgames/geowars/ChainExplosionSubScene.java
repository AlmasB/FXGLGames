package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ChainExplosionSubScene extends GameSubScene {

    public ChainExplosionSubScene() {
        super(getAppWidth(), getAppHeight());
    }

    public void addTargets(Entity originalPlayer, List<Entity> entities) {
        var player = getGameWorld().spawn("Player");

        this.getGameScene().getViewport().bindToEntity(originalPlayer, getAppWidth() / 2, getAppHeight() / 2);

        var last = player;

        for (int i = 0; i < entities.size(); i++) {
            var target = entities.get(i);

            final int j = i;

            animationBuilder()
                    .onFinished(() -> {
                        if (j == entities.size() - 1) {

                            entities.forEach(e -> FXGL.<GeoWarsApp>getAppCast().killEnemy(e));

                            getSceneService().popSubScene();
                        }
                    })
                    .interpolator(FXGLMath.random(Interpolators.values()).get().EASE_OUT())
                    .duration(Duration.seconds(0.25))
                    .delay(Duration.seconds(i * 0.25))
                    .translate(player)
                    .from(last.getPosition())
                    .to(target.getPosition().add(0, 0))
                    .buildAndPlay(this);

            last = target;
        }
    }
}
