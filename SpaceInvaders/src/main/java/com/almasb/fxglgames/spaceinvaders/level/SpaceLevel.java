package com.almasb.fxglgames.spaceinvaders.level;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class SpaceLevel {

    private List<Entity> enemies = new ArrayList<>();

    private Pane storyPane = new Pane();
    private Pane rootPane;

    public SpaceLevel() {
        Rectangle bg = new Rectangle(getAppWidth() - 20, 200, Color.color(0, 0, 0, 0.6));
        bg.setArcWidth(25);
        bg.setArcHeight(25);
        bg.setStroke(Color.color(0.1, 0.2, 0.86, 0.76));
        bg.setStrokeWidth(3);

        storyPane.setTranslateX(10);
        storyPane.setTranslateY(25);

        rootPane = new Pane(bg, storyPane);
        rootPane.setTranslateX(10);
        rootPane.setTranslateY(getAppHeight() - 200);
    }

    public abstract void init();

    public void onUpdate(double tpf) {

    }

    public void destroy() {

    }

    public void playInCutscene(Runnable onFinished) {
        onFinished.run();
    }

    public void playOutCutscene(Runnable onFinished) {
        onFinished.run();
    }

    protected void showStoryPane() {
        addUINode(rootPane);
    }

    protected void hideStoryPane() {
        removeUINode(rootPane);
    }

    protected void updateStoryText(Node node) {
        storyPane.getChildren().setAll(node);
    }

    protected void updateAlienStoryText(String data) {
        List<Text> texts = new ArrayList<>();
        double bounds = 0;

        List<Character> characters = new ArrayList<>();

        for (char c : data.toCharArray()) {
            characters.add(c);
        }

        for (char c : characters) {
            Text t = new Text(c + "");
            t.setFill(Color.WHITE);
            t.setFont(Font.font(24));
            t.setTranslateX(bounds);

            texts.add(t);

            t.setUserData(new Point2D(t.getTranslateX(), t.getTranslateY()));

            bounds += t.getLayoutBounds().getWidth();
        }

        bounds = 0;

        Collections.shuffle(texts);

        for (Text t : texts) {
            t.setTranslateX(bounds);

            bounds += t.getLayoutBounds().getWidth();

            Point2D p = (Point2D) t.getUserData();

            animationBuilder()
                    .duration(Duration.seconds(2))
                    .delay(Duration.seconds(1))
                    .translate(t)
                    .from(new Point2D(t.getTranslateX(), t.getTranslateY()))
                    .to(p)
                    .buildAndPlay();
        }

        storyPane.getChildren().setAll(texts);
    }

    protected void addEnemy(Entity entity) {
        enemies.add(entity);
    }

    public boolean isFinished() {
        return enemies.stream().noneMatch(Entity::isActive);
    }

    protected Entity spawnEnemy(double x, double y) {
        Entity enemy = spawn("Enemy", x, y);

        addEnemy(enemy);

        animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(FXGLMath.random(0.0, 1.0) * 2))
                .scale(enemy)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();

        return enemy;
    }
}
