package com.almasb.fxglgames.slotmachine;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.slotmachine.components.LeverComponent;
import com.almasb.fxglgames.slotmachine.components.WheelComponent;

import java.util.stream.IntStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineFactory {

    public Entity buildLever() {
        return FXGL.entityBuilder()
                .at(1030, 340)
                .view("lever0.png")
                .with(new LeverComponent())
                .build();
    }

    public Entity[] buildBackground() {
        return new Entity[] {
                FXGL.entityBuilder().view("bg.png").build(),
                FXGL.entityBuilder().at(910, 410).view("coin.gif").build()
        };
    }

    public Entity[] buildWheels() {
        return IntStream.range(0, 5)
                .mapToObj(i -> {
                    return FXGL.entityBuilder()
                            .type(SlotMachineType.WHEEL)
                            .at(50 + 240 * i, 70)
                            .view("elements.png")
                            .with(new WheelComponent())
                            .build();
                }).toArray(Entity[]::new);
    }
}
