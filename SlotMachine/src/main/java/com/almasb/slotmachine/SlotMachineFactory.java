package com.almasb.slotmachine;

import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.slotmachine.control.LeverControl;
import com.almasb.slotmachine.control.WheelControl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlotMachineFactory {

    public GameEntity buildLever() {
        return Entities.builder()
                .at(1030, 340)
                .viewFromTexture("lever0.png")
                .with(new LeverControl())
                .build();
    }

    public GameEntity[] buildBackground() {
        return new GameEntity[] {
                Entities.builder().viewFromTexture("bg.png").build(),
                Entities.builder().at(910, 410).viewFromTexture("coin.gif").build()
        };
    }

    public GameEntity[] buildWheels() {
        return IntStream.range(0, 5)
                .mapToObj(i -> {
                    return Entities.builder()
                            .type(EntityType.WHEEL)
                            .at(50 + 240 * i, 70)
                            .viewFromTexture("elements.png")
                            .with(new WheelControl())
                            .build();
                })
                .collect(Collectors.toList())
                .toArray(new GameEntity[0]);
    }
}
