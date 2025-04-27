

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

        // Create the component with our mock FXGL interface
        laserBeam = new LaserBeamComponent(fxglMock);

        // Set the entity field using reflection since we can't directly access it
        try {
            java.lang.reflect.Field entityField = com.almasb.fxgl.entity.component.Component.class.getDeclaredField("entity");
            entityField.setAccessible(true);
            entityField.set(laserBeam, entityMock);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set entity field", e);
        }

        // Now we can call onAdded() (no parameters)
        laserBeam.onAdded();
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0.5, 0.1})
    void testOnUpdateDecrementsLaserMeter(double initialValue) {
        double tpf = 0.016; // typical time per frame
        when(fxglMock.getd("laserMeter")).thenReturn(initialValue);

        // Call onUpdate
        laserBeam.onUpdate(tpf);

        // Verify inc was called with correct parameters
        verify(fxglMock).inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);
    }

    @Test
    void testOnUpdateKeepsEntityWhenMeterAboveZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(0.5); // Meter still has charge

        // Call onUpdate
        laserBeam.onUpdate(tpf);

        // Verify entity was not removed
        verify(entityMock, never()).removeFromWorld();
    }

    @Test
    void testOnUpdateRemovesEntityWhenMeterAtZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(0.0);

        // Call onUpdate
        laserBeam.onUpdate(tpf);

        // Verify entity was removed
        verify(fxglMock).set("laserMeter", 0.0);
        verify(entityMock).removeFromWorld();
    }

    @Test
    void testOnUpdateRemovesEntityWhenMeterBelowZero() {
        double tpf = 0.016;
        when(fxglMock.getd("laserMeter")).thenReturn(-0.1);

        // Call onUpdate
        laserBeam.onUpdate(tpf);

        // Verify meter was reset to 0 and entity was removed
        verify(fxglMock).set("laserMeter", 0.0);
        verify(entityMock).removeFromWorld();
    }

    @Test
    void testOnUpdateWithVariousTimeValues() {
        // Test with different tpf values
        double[] tpfValues = {0.016, 0.032, 0.064};
        double initialMeter = 1.0;

        for (double tpf : tpfValues) {
            // Reset mocks
            reset(entityMock, fxglMock);

            // Set up initial meter value
            when(fxglMock.getd("laserMeter")).thenReturn(initialMeter);

            // Call onUpdate
            laserBeam.onUpdate(tpf);

            // Verify correct decrement
            verify(fxglMock).inc("laserMeter", -Config.LASER_METER_DEPLETE * tpf);
        }
    }
}
