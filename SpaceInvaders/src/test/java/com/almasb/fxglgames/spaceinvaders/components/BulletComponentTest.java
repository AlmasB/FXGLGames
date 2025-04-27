package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BulletComponentTest {
    private BulletComponent bulletComponent;
    private OwnerComponent ownerMock;
    private Entity entityMock;
    @BeforeEach
    void setUp() throws Exception {
        ownerMock = mock(OwnerComponent.class);
        entityMock = mock(Entity.class);
        bulletComponent = new BulletComponent(10.5, ownerMock, entityMock);

        Field entityField = bulletComponent.getClass().getSuperclass().getDeclaredField("entity");
        entityField.setAccessible(true);
        entityField.set(bulletComponent, entityMock);
    }

    @ParameterizedTest(name = "{index} => tpf={0}, expected={1}")
    @CsvSource({
            "0.016, -0.168",
            "0, -0",
            "-1.1, 11.55"
    })
    void onUpdate_translateY_whenPlayer(double tpf, double expected) {
        when(ownerMock.getValue()).thenReturn(SpaceInvadersType.PLAYER);

        bulletComponent.onUpdate(tpf);

        verify(ownerMock, times(1)).getValue();
        verify(entityMock, times(1)).translateY(expected);
    }

    @ParameterizedTest(name = "{index} => tpf={0}, expected={1}")
    @CsvSource({
            "0.016, 0.168",
            "0, 0",
            "-1.1, -11.55"
    })
    void upDate_translateY_whenEnemy(double tpf, double expected) {
        when(ownerMock.getValue()).thenReturn(SpaceInvadersType.ENEMY);

        bulletComponent.onUpdate(tpf);

        verify(ownerMock, times(1)).getValue();
        verify(entityMock, times(1)).translateY(expected);
    }
}