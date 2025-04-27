package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class BossComponentTest {

    private BossComponent bossComponent;
    private Entity mockEntity;
    private CollidableComponent mockCollidable;
    private EventBus mockEventBus;

    @BeforeEach
    void setUp() {
        bossComponent = new BossComponent();

        mockEntity = mock(Entity.class);
        mockCollidable = mock(CollidableComponent.class);
        mockEventBus = mock(EventBus.class);

        // Using reflection to set the entity
//        bossComponent.setEntityViaReflection(bossComponent, mockEntity);
    }

    @Test
    void die_ShouldDisableEntity_SpawnExplosions_RemoveEntityAndFireEvent() {
        // Arrange
        when(mockEntity.getComponent(CollidableComponent.class)).thenReturn(mockCollidable);
        when(mockEntity.getCenter()).thenReturn(new Point2D(0, 0));

        try (MockedStatic<FXGL> fxgl = mockStatic(FXGL.class);
             MockedStatic<FXGLMath> fxglMath = mockStatic(FXGLMath.class)) {

            // Mock FXGL static methods
            fxgl.when(FXGL::getEventBus).thenReturn(mockEventBus);
            fxgl.when(() -> FXGL.runOnce(any(), any(Duration.class))).thenAnswer(invocation -> {
                Runnable action = invocation.getArgument(0);
                action.run(); // run immediately for testing
                return null;
            });

            fxgl.when(() -> FXGL.spawn(eq("Explosion"), any(Point2D.class))).thenReturn(mock(Entity.class));

            // Mock FXGLMath.randomPoint2D()
            fxglMath.when(FXGLMath::randomPoint2D).thenReturn(new Point2D(1, 1));

            // Act
            bossComponent.die();

            // Assert
            verify(mockCollidable).setValue(false);
            verify(mockEntity).setUpdateEnabled(false);

            // Explosion should be spawned 5 times
            fxgl.verify(() -> FXGL.spawn(eq("Explosion"), any(Point2D.class)), times(5));

            verify(mockEntity).removeFromWorld();
            verify(mockEventBus).fireEvent(any(GameEvent.class));
        }
    }
}
