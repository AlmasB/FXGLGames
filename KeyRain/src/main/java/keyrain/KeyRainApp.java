package keyrain;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.TriggerListener;
import com.almasb.fxgl.input.view.KeyView;
import javafx.scene.CacheHint;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.util.Duration.seconds;
import static keyrain.EntityType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class KeyRainApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Key Rain");
        settings.setWidth(1700);
        settings.setHeightFromRatio(16/9.0);
    }

    @Override
    protected void initInput() {
        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onActionBegin(Trigger trigger) {
                if (trigger.isKey()) {
                    var keyTrigger = (KeyTrigger) trigger;
                    var keyCode = keyTrigger.getKey();

                    var keyEntity = byType(KEY).stream()
                            .filter(e -> e.getObject("keyCode").equals(keyCode) && e.hasComponent(ProjectileComponent.class))
                            .max(Comparator.comparingDouble(e -> e.getY()));

                    if (keyEntity.isPresent()) {
                        var e = keyEntity.get();
                        e.removeComponent(ProjectileComponent.class);

                        var view = (KeyView) e.getViewComponent().getChildren().get(0);

                        animationBuilder()
                                .onFinished(() -> {
                                    despawnWithScale(e, Duration.seconds(0.5), Interpolators.EXPONENTIAL.EASE_OUT());
                                })
                                .duration(Duration.seconds(0.26))
                                .animate(view.keyColorProperty())
                                .from(view.getKeyColor())
                                .to(Color.YELLOW)
                                .buildAndPlay();

                        inc("score", +3);
                    } else {
                        inc("score", -5);
                    }
                }
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("lives", 3);
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        getGameWorld().addEntityFactory(new KeyRainFactory());

        spawn("base", 0, getAppHeight() - 150);

        run(() -> spawn("key", random(0, getAppWidth() - 50), -50), seconds(0.35));

        runOnce(() -> {
            var input = getSceneService().getInput();
            input.rebind(input.getActionByName("Screenshot"), KeyCode.SPACE);
        }, seconds(0.1));
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(KEY, PLAYER_BASE, (key, base) -> {
            key.removeFromWorld();
            inc("lives", -1);

            if (geti("lives") == 0) {
                showMessage("Game Over!", () -> getGameController().exit());
            }
        });
    }

    @Override
    protected void initUI() {
        var textLives = getUIFactoryService().newText("", Color.WHITE, 16.0);
        textLives.textProperty().bind(getip("lives").asString("Lives: %d"));

        addUINode(textLives, 25, 25);

        var textScore = getUIFactoryService().newText("", Color.WHITE, 16.0);
        textScore.textProperty().bind(getip("score").asString("Score: %d"));

        addUINode(textScore, 25, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
