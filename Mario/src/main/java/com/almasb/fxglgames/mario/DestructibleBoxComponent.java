package com.almasb.fxglgames.mario;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DestructibleBoxComponent extends Component {

    private static final List<Texture> textures = new ArrayList<>();

    public void explode() {
        if (textures.isEmpty()) {
            EntityView view = (EntityView) entity.getViewComponent().getView();
            ImageView imageView = (ImageView) view.getNodes().get(0);
            var texture = new Texture(imageView.getImage());

            var rects = new ArrayList<Rectangle2D>();

            for (int y = 0; y < texture.getHeight(); y += 5) {
                for (int x = 0; x < texture.getWidth(); x += 5) {
                    rects.add(new Rectangle2D(x, y, 5, 5));
                }
            }

            rects.forEach(rect -> {
                var subtexture = texture.subTexture(rect);

                subtexture.getProperties().put("point", new Point2D(rect.getMinX(), rect.getMinY()));

                textures.add(subtexture);
            });
        }


        textures.forEach(texture -> {
            FXGL.entityBuilder()
                    .at(entity.getPosition().add((Point2D) texture.getProperties().get("point")))
                    .view(new Texture(texture.getImage()))
                    .with(new ExpireCleanComponent(Duration.seconds(2)).animateOpacity())
                    .with(new ProjectileComponent(FXGLMath.randomPoint2D(), FXGLMath.random(15, 100)))
                    .buildAndAttach();
        });

        entity.removeFromWorld();
    }
}
