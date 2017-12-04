package com.almasb.fxglgames.slotmachine;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.slotmachine.control.LeverControl;
import com.almasb.fxglgames.slotmachine.control.WheelControl;

import java.util.stream.IntStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineFactory {

    public Entity buildLever() {
        return Entities.builder()
                .at(1030, 340)
                .viewFromTexture("lever0.png")
                .with(new LeverControl())
                .build();
    }

    public Entity[] buildBackground() {
        return new Entity[] {
                Entities.builder().viewFromTexture("bg.png").build(),
                Entities.builder().at(910, 410).viewFromTexture("coin.gif").build()
        };
    }

    public Entity[] buildWheels() {
        return IntStream.range(0, 5)
                .mapToObj(i -> {
                    return Entities.builder()
                            .type(SlotMachineType.WHEEL)
                            .at(50 + 240 * i, 70)
                            .viewFromTexture("elements.png")
                            .with(new WheelControl())
                            .build();
                }).toArray(Entity[]::new);
    }
}
