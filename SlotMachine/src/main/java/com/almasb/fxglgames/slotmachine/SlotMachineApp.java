package com.almasb.fxglgames.slotmachine;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxglgames.slotmachine.components.WheelComponent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineApp extends GameApplication {

    private static final int START_MONEY = 500;

    private SlotMachineFactory entityFactory;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Slot Machine");
        settings.setVersion("0.1");
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("money", START_MONEY);
    }

    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    @Override
    protected void initGame() {
        entityFactory = new SlotMachineFactory();

        getGameWorld().addEntities(entityFactory.buildWheels());
        getGameWorld().addEntities(entityFactory.buildBackground());
        getGameWorld().addEntity(entityFactory.buildLever());
    }

    @Override
    protected void initUI() {
        Text textMoney = new Text();

        textMoney.layoutBoundsProperty().addListener((observable, oldValue, newBounds) -> {
            textMoney.setTranslateX(getAppWidth() / 2 - newBounds.getWidth() / 2);
        });

        textMoney.setTranslateY(50);
        textMoney.setFont(Font.font(36));
        textMoney.setFill(Color.WHITE);
        textMoney.textProperty().bind(getip("money").asString("$%d"));

        addUINode(textMoney);
    }

    private List<Integer> spinValues = new ArrayList<>();

    public boolean isMachineSpinning() {
        return getWheels().stream().anyMatch(WheelComponent::isSpinning);
    }

    public void spin() {
        getWheels().forEach(WheelComponent::spin);
    }

    public List<WheelComponent> getWheels() {
        return getGameWorld()
                .getEntitiesByType(SlotMachineType.WHEEL)
                .stream()
                .map(e -> e.getComponent(WheelComponent.class))
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

        inc("money", reward);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
