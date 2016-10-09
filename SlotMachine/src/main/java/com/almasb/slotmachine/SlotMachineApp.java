package com.almasb.slotmachine;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.slotmachine.control.WheelControl;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineApp extends GameApplication {

    private SlotMachineFactory entityFactory;
    private IntegerProperty money;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Slot Machine");
        settings.setVersion("0.1");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Override
    protected void initGame() {
        money = new SimpleIntegerProperty(500);
        entityFactory = new SlotMachineFactory();

        getGameWorld().addEntities(entityFactory.buildWheels());
        getGameWorld().addEntities(entityFactory.buildBackground());
        getGameWorld().addEntity(entityFactory.buildLever());
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {
        Text textMoney = new Text();

        textMoney.layoutBoundsProperty().addListener((observable, oldValue, newBounds) -> {
            textMoney.setTranslateX(getWidth() / 2 - newBounds.getWidth() / 2);
        });

        textMoney.setTranslateY(50);
        textMoney.setFont(Font.font(36));
        textMoney.setFill(Color.WHITE);
        textMoney.textProperty().bind(money.asString("$%d"));

        getGameScene().addUINode(textMoney);
    }

    @Override
    protected void onUpdate(double tpf) {}

    private List<Integer> spinValues = new ArrayList<>();

    public boolean isMachineSpinning() {
        return getWheels().stream()
                .filter(WheelControl::isSpinning)
                .count() > 0;
    }

    public void spin() {
        getWheels().forEach(WheelControl::spin);
    }

    public List<WheelControl> getWheels() {
        return getGameWorld()
                .getEntitiesByType(EntityType.WHEEL)
                .stream()
                .map(e -> e.getControlUnsafe(WheelControl.class))
                .collect(Collectors.toList());
    }

    public void onSpinFinished(int value) {
        spinValues.add(value);

        if (spinValues.size() == 5) {
            spinValues.stream()
                    .collect(Collectors.groupingBy(i -> i))
                    .values()
                    .stream()
                    .mapToInt(List::size)
                    .max()
                    .ifPresent(this::giveMoney);

            spinValues.clear();
        }
    }

    public void giveMoney(int highestMatch) {
        int reward;

        if (highestMatch > 1) {
            reward = highestMatch * highestMatch * 50;
        } else {
            reward = -100;
        }

        money.set(money.get() + reward);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
