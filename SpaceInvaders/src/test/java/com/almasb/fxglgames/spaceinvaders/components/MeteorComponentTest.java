package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import com.almasb.fxgl.dsl.FXGL;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MeteorComponentTest {

    private MeteorComponent meteorComponent;
    private Entity entityMock;
    private static MockedStatic<FXGL> fxglMock;

    @BeforeAll
    static void beforeAll() {
        fxglMock = mockStatic(FXGL.class);
        fxglMock.when(FXGL::getAppWidth).thenReturn(800);
        fxglMock.when(FXGL::getAppHeight).thenReturn(600);
    }

    @AfterAll
    static void afterAll() {
        if (fxglMock != null) {
            fxglMock.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        entityMock = mock(Entity.class);
        meteorComponent = new MeteorComponent();

        Field entityField = meteorComponent.getClass().getSuperclass().getDeclaredField("entity");
        entityField.setAccessible(true);
        entityField.set(meteorComponent, entityMock);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 100, 28.2842712474619, 28.2842712474619",
            "200, 200, 28.2842712474619, 28.2842712474619",
            "500, 500, -28.2842712474619, -28.2842712474619"
    })
    @DisplayName("Test onAdded initializes correctly based on position")
    void testOnAddedInitializesVelocity(double x, double y, double velX, double velY) {
        when(entityMock.getX()).thenReturn(x);
        when(entityMock.getY()).thenReturn(y);

        try (MockedStatic<FXGLMath> fxglMathMock = mockStatic(FXGLMath.class)) {
            fxglMathMock.when(() -> FXGLMath.random(40, 50)).thenReturn(40);

            meteorComponent.onAdded();

            assertEquals(new Point2D(velX, velY), meteorComponent.getVelocity());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "200, 150",
            "700, 550"
    })
    @DisplayName("Test onUpdate moves and rotates entity correctly")
    void testOnUpdateMovesAndRotatesEntity(double x, double y) {
        when(entityMock.getX()).thenReturn(x);
        when(entityMock.getY()).thenReturn(y);

        meteorComponent.onAdded();
        meteorComponent.onUpdate(0.016);

        verify(entityMock, times(1)).translate(any(Point2D.class));
        verify(entityMock, times(1)).rotateBy(anyDouble());
    }
}
