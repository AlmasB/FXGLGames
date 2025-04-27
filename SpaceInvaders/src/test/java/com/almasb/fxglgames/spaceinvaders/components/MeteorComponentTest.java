package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import com.almasb.fxgl.dsl.FXGL;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;

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
    void setUp() {
        entityMock = mock(Entity.class);
        meteorComponent = new MeteorComponent(entityMock);
    }

    @ParameterizedTest
    @CsvSource({
            "100, 100",
            "10, 10",
            "400, 300"
    })
    @DisplayName("Test onAdded initializes correctly based on position")
    void testOnAddedInitializesVelocity(double x, double y) {
        when(entityMock.getX()).thenReturn(x);
        when(entityMock.getY()).thenReturn(y);

        meteorComponent.onAdded();

        verify(entityMock, never()).translate(any(Point2D.class));
        verify(entityMock, never()).rotateBy(anyDouble());
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

        verify(entityMock, atLeastOnce()).translate(any(Point2D.class));
        verify(entityMock, atLeastOnce()).rotateBy(anyDouble());
    }
}
