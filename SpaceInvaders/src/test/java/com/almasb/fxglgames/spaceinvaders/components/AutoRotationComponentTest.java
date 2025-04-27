package com.almasb.fxglgames.spaceinvaders.components;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class AutoRotationComponentTest {
    private AutoRotationComponent autoRotation;
    private Entity entityMock;

    @BeforeEach
    void setUp() {
        entityMock = mock(Entity.class);
        autoRotation = new AutoRotationComponent();
        autoRotation.setEntity(entityMock);
    }

    @ParameterizedTest
    @CsvSource({
            "-50.5,-100.5,-50.5,-100.5",
            "0,0,0,0",
            "50.1,100.8,50.1,100.8"
    })
    void testOnAddedStoresInitialPosition(double x, double y, double expectedX, double expectedY) {
        when(entityMock.getPosition()).thenReturn(new Point2D(x, y));

        autoRotation.onAdded();

        verify(entityMock, times(1)).getPosition();
        assertEquals(new Point2D(expectedX,expectedY),autoRotation.getPrev());
    }

    @ParameterizedTest
    @CsvSource({
            "5,5,30.0,27.0",
            "10,10,45.0,40.5",
            "15,15,90.0,81.0"
    })
    void testOnUpdateWhenStationaryKeepsRotation(double x, double y, double initialRotation, double expectedRotation) {
        when(entityMock.getPosition()).thenReturn(new Point2D(x, y));
        when(entityMock.getRotation()).thenReturn(initialRotation);

        autoRotation.onAdded();
        autoRotation.onUpdate(0.016);

        verify(entityMock).setRotation(expectedRotation);
    }
}
