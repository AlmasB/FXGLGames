

package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.spaceinvaders.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.Mockito.*;

class LaserBeamComponentTest {
    private LaserBeamComponent laserBeam;
    private Entity entityMock;
    private LaserBeamComponent.FXGLInterface fxglMock;

    @BeforeEach
    void setUp() {
        entityMock = mock(Entity.class);
        fxglMock = mock(LaserBeamComponent.FXGLInterface.class);
        laserBeam = new LaserBeamComponent(fxglMock);

        try {
            java.lang.reflect.Field entityField = com.almasb.fxgl.entity.component.Component.class.getDeclaredField("entity");
            entityField.setAccessible(true);
            entityField.set(laserBeam, entityMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set entity field", e);
        }
        laserBeam.onAdded();
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0.5, 0.1})
    void testOnUpdateDecrementsLaserMeter(double initialValue) {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(initialValue);

        laserBeam.onUpdate(tpf);

        verify(fxglMock).inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);
    }

    @Test
    void testOnUpdateKeepsEntityWhenMeterAboveZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(0.5); // Meter still has charge

        laserBeam.onUpdate(tpf);

        verify(entityMock, never()).removeFromWorld();
    }

    @Test
    void testOnUpdateRemovesEntityWhenMeterAtZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(0.0);

        laserBeam.onUpdate(tpf);

        verify(fxglMock).set("laserMeter", 0.0);
        verify(entityMock).removeFromWorld();
    }

    @Test
    void testOnUpdateRemovesEntityWhenMeterBelowZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(-0.1);
        
        laserBeam.onUpdate(tpf);

        verify(fxglMock).set("laserMeter", 0.0);
        verify(entityMock).removeFromWorld();
    }

    @Test
    void testOnUpdateWithVariousTimeValues() {
        double[] tpfValues = {0.016, 0.032, 0.064};
        double initialMeter = 1.0;

        for (double tpf : tpfValues) {
            reset(entityMock, fxglMock);

            when(fxglMock.getd("laserMeter")).thenReturn(initialMeter);

            laserBeam.onUpdate(tpf);

            verify(fxglMock).inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);
        }
    }
}
