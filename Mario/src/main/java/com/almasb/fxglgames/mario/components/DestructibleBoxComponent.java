package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.random;
import static com.almasb.fxgl.core.math.FXGLMath.randomPoint2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DestructibleBoxComponent extends Component {

    private static Texture texture = null;

    public void explode() {
        if (texture == null) {
            EntityView view = (EntityView) entity.getViewComponent().getView();
            ImageView imageView = (ImageView) view.getNodes().get(0);
            texture = new Texture(imageView.getImage());
        }

        var emitter = ParticleEmitters.newExplosionEmitter(40);
        emitter.setSourceImage(texture);
        emitter.setMaxEmissions(1);
        emitter.setNumParticles(42);
        emitter.setSize(10, 10);
        emitter.setScaleFunction(i -> {
            var s = random(0.01, 0.07);
            return new Point2D(s, s);
        });
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSpawnPointFunction(i -> new Point2D((i % 6) * 10 - 25, (i / 7) * 10 - 25));
        emitter.setVelocityFunction(i -> randomPoint2D().multiply(random(5, 25)));
        emitter.setExpireFunction(i -> Duration.seconds(1.5));

        var comp = new ParticleComponent(emitter);
        comp.setOnFinished(() -> entity.removeFromWorld());

        entity.getViewComponent().setOpacity(0);
        entity.addComponent(comp);
    }
}
