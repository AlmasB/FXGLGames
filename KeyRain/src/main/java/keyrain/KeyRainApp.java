package keyrain;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.TriggerListener;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

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
        //settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initInput() {
        // TODO: KeyTrigger / MouseTrigger, more specific ...

        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onActionBegin(Trigger trigger) {
                if (trigger.isKey()) {
                    var keyTrigger = (KeyTrigger) trigger;
                    var keyCode = keyTrigger.getKey();

                    byType(KEY).stream()
                            .filter(e -> e.getObject("keyCode").equals(keyCode))
                            .max(Comparator.comparingDouble(e -> e.getY()))
                            .ifPresent(e -> e.removeFromWorld());
                }
            }
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("lives", 3);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        getGameWorld().addEntityFactory(new KeyRainFactory());

        spawn("base", 0, getAppHeight() - 150);

        run(() -> spawn("key", random(0, getAppWidth() - 50), -50), seconds(0.5));

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

    public static void main(String[] args) {
        launch(args);
    }
}
