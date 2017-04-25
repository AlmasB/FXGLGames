package com.almasb.fxglgames.slidingpuzzle;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SlidingPuzzleApp extends GameApplication {

    private static final int TILE_SIZE = 120;
    private static final int ROW_SIZE = 600 / TILE_SIZE;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(600);
        settings.setHeight(600);
        settings.setTitle("SlidingPuzzleApp");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        Texture original = getAssetLoader().loadTexture("image.jpg");

        List<Texture> textures = IntStream.range(0, ROW_SIZE * ROW_SIZE)
                .mapToObj(i -> new Point2D(i % ROW_SIZE, i / ROW_SIZE))
                .map(p -> original.subTexture(new Rectangle2D(p.getX() * TILE_SIZE, p.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE)))
                .collect(Collectors.toList());

        Collections.shuffle(textures);

        for (int i = 0; i < textures.size(); i++) {
            Texture t = textures.get(i);
            t.setTranslateX(i % ROW_SIZE * TILE_SIZE);
            t.setTranslateY(i / ROW_SIZE * TILE_SIZE);

            getGameScene().addUINode(t);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
