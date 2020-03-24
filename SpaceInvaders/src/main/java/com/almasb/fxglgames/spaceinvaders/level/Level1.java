package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spaceinvaders.SpaceInvadersType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMIES_PER_ROW;
import static com.almasb.fxglgames.spaceinvaders.Config.ENEMY_ROWS;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Level1 extends SpaceLevel {

    @Override
    public void playInCutscene(Runnable onFinished) {

        boolean doNotPlayCutscene = true;
        if (doNotPlayCutscene) {
            onFinished.run();
            return;
        }

        placeGenerals();

        showStoryPane();

        Text text = getUIFactoryService().newText("HQ: Attention, V.I.T. generals! Your mission is to find and destroy the alien invaders...", Color.WHITE, 24.0);
        text.setWrappingWidth(getAppWidth() - 50);

        updateStoryText(text);

        runOnce(() -> {
            text.setText("HQ: Wait a sec... we are reading alien signals.");
        }, Duration.seconds(3));

        runOnce(() -> {

            placeBoss();

        }, Duration.seconds(6));

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();

        }, Duration.seconds(26));
    }

    private void placeGenerals() {
        for (int i = 0; i < 5; i++) {
            spawn("General", 25 + i*100, getAppHeight() - (i % 2 == 0 ? 300 : 400));
        }
    }

    private void placeBoss() {
        Texture boss = texture("bosses/boss_final.png");
        boss.setOpacity(0);
        boss.setTranslateX(getAppWidth() / 2 - 101);
        boss.setTranslateY(0);

        var view = new GameView(boss, 4000);

        getGameScene().addGameView(view);

        animationBuilder()
                .duration(Duration.seconds(2))
                .fadeIn(boss)
                .buildAndPlay();

        updateAlienStoryText("Humans, you are now in MY domain!");

        runOnce(() -> {
            updateAlienStoryText("I am taking your generals.");
        }, Duration.seconds(5));

        runOnce(() -> {
            getGameWorld().getEntitiesByType(SpaceInvadersType.NPC_GENERAL).forEach(e -> {
                animationBuilder()
                        .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                        .onFinished(() -> e.removeFromWorld())
                        .duration(Duration.seconds(2.5))
                        .translate(e)
                        .from(e.getPosition())
                        .to(new Point2D(FXGLMath.random(0, getAppWidth() - 50), -50))
                        .buildAndPlay();
            });
        }, Duration.seconds(8));

        runOnce(() -> {
            updateAlienStoryText("My fleet will destroy you.");
        }, Duration.seconds(12));

        runOnce(() -> {
            animationBuilder()
                    .onFinished(() -> getGameScene().removeGameView(view))
                    .duration(Duration.seconds(2))
                    .fadeOut(boss)
                    .buildAndPlay();
        }, Duration.seconds(13.5));

//        runOnce(() -> {
//            showInstructions();
//        }, Duration.seconds(18));
    }

    private void showInstructions() {
//        FXGLTextFlow flow = FXGL.getUIFactory().newTextFlow()
//                .append("Press ", Color.WHITE).append(KeyCode.A, Color.RED).append(" to move left\n", Color.WHITE)
//                .append("Press ", Color.WHITE).append(KeyCode.D, Color.RED).append(" to move right\n", Color.WHITE)
//                .append("Press ", Color.WHITE).append(MouseButton.PRIMARY, Color.RED).append(" to shoot!", Color.WHITE);
//
//        updateStoryText(flow);
    }

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                runOnce(() -> {
                    Entity enemy = spawnEnemy(50, 50);
                    enemy.addComponent(new MoveComponent());
                }, Duration.seconds(t));

                t += 0.1;
            }
        }
    }

    private static class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(curveFunction().add(getAppWidth() / 2, getAppHeight() / 2 - 100));

            t += tpf;
        }

        private Point2D curveFunction() {
            double x = cos(5*t) - cos(2*t);
            double y = sin(3*t) - sin(t);

            return new Point2D(x, -y).multiply(85);
        }
    }
}
