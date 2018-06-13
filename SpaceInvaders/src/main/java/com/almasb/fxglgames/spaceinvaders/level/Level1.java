package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FXGLTextFlow;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.app.DSLKt.*;
import static com.almasb.fxgl.util.BackportKt.forEach;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;
import static java.lang.Math.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level1 extends SpaceLevel {

    @Override
    public void playInCutscene(Runnable onFinished) {

        boolean b = true;
        if (b) {
            onFinished.run();
            return;
        }

        placeGenerals();

        showStoryPane();

        Text text = FXGL.getUIFactory().newText("HQ: Attention, V.I.T. generals! Your mission is to find and destroy the alien invaders...", Color.WHITE, 22.0);
        text.setFont(Font.font(24.0));
        text.setWrappingWidth(FXGL.getAppWidth() - 50);

        updateStoryText(text);

        runOnce(() -> {
            text.setText("HQ: Wait... we are reading alien signals.");
        }, Duration.seconds(5));

        runOnce(() -> {

            placeBoss();

        }, Duration.seconds(7));

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();

        }, Duration.seconds(33));
    }

    private void placeGenerals() {
        for (int i = 0; i < 5; i++) {
            spawn("General", 25 + i*100, FXGL.getAppHeight() - (i % 2 == 0 ? 300 : 400));
        }
    }

    private void placeBoss() {
        Texture boss = texture("bosses/boss_final.png");

        EntityView view = new EntityView(boss);
        view.setOpacity(0);

        FXGL.getApp().getGameScene().addGameView(view);

        fadeIn(view, Duration.seconds(2)).startInPlayState();

        view.setTranslateX(FXGL.getAppWidth() / 2 - 101);
        view.setTranslateY(0);

        updateAlienStoryText("Humans, you are now in MY domain!");

        runOnce(() -> {
            updateAlienStoryText("I am taking your generals.");
        }, Duration.seconds(5));

        runOnce(() -> {
            forEach(FXGL.getApp().getGameWorld().getEntitiesByType(SpaceInvadersType.NPC_GENERAL), e -> {
                Entities.animationBuilder()
                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                        .onFinished(() -> e.removeFromWorld())
                        .duration(Duration.seconds(3))
                        .translate(e)
                        .from(e.getPosition())
                        .to(new Point2D(FXGLMath.random(0, FXGL.getAppWidth() - 50), -50))
                        .buildAndPlay();
            });
        }, Duration.seconds(8));

        runOnce(() -> {
            updateAlienStoryText("My fleet will destroy you.");
        }, Duration.seconds(12));

        runOnce(() -> {
            fadeOut(view, Duration.seconds(2), () -> {
                FXGL.getApp().getGameScene().removeGameView(view, RenderLayer.DEFAULT);
            }).startInPlayState();
        }, Duration.seconds(15));

        runOnce(() -> {
            showInstructions();
        }, Duration.seconds(18));
    }

    private void showInstructions() {
        FXGLTextFlow flow = FXGL.getUIFactory().newTextFlow()
                .append("Press ", Color.WHITE).append(KeyCode.A, Color.RED).append(" to move left\n", Color.WHITE)
                .append("Press ", Color.WHITE).append(KeyCode.D, Color.RED).append(" to move right\n", Color.WHITE)
                .append("Press ", Color.WHITE).append(MouseButton.PRIMARY, Color.RED).append(" to shoot!", Color.WHITE);

        updateStoryText(flow);
    }

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                FXGL.getMasterTimer().runOnceAfter(() -> {

                    Entity enemy = spawnEnemy(50, 50);

                    enemy.addComponent(new MoveComponent());

                }, Duration.seconds(t));

                t += 0.1;
            }
        }
    }

    private class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(curveFunction().add(FXGL.getAppWidth() / 2, FXGL.getAppHeight() / 2 - 100));

            t += tpf;
        }

        private Point2D curveFunction() {
            double x = cos(5*t) - cos(2*t);
            double y = sin(3*t) - sin(t);

            return new Point2D(x, -y).multiply(85);
        }
    }
}
