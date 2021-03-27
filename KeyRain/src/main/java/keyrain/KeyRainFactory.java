package keyrain;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.input.view.KeyView;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static keyrain.EntityType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class KeyRainFactory implements EntityFactory {

    private static final List<String> ABC = new ArrayList<>();

    static {
        for (char c = 'A'; c <= 'Z'; c++) {
            ABC.add(c + "");
        }
    }

    @Spawns("key")
    public Entity newKey(SpawnData data) {
        var key = FXGLMath.random(ABC).get();
        var keyCode = KeyCode.valueOf(key);

        var view = new KeyView(keyCode, Color.BLUE, 36);
        view.setCache(true);
        view.setCacheHint(CacheHint.SCALE);

        return entityBuilder(data)
                .type(KEY)
                .bbox(new HitBox(BoundingShape.box(45, 58)))
                .view(view)
                .with(
                        new ProjectileComponent(new Point2D(0, 1), random(100, 300))
                                .allowRotation(false)
                )
                .with("keyCode", keyCode)
                .collidable()
                .build();
    }

    @Spawns("base")
    public Entity newBase(SpawnData data) {
        return entityBuilder(data)
                .type(PLAYER_BASE)
                .viewWithBBox(new Rectangle(getAppWidth(), 50, Color.BROWN))
                .collidable()
                .build();
    }
}
