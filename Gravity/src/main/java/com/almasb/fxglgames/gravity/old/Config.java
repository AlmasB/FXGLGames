package com.almasb.fxglgames.gravity.old;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.almasb.fxgl.core.math.Vec2;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;

public final class Config {
    /**
     * No outside instances
     */
    private Config() {}

    /**
     * The config singleton for convenient resource
     * management since we don't need to use classloaders
     * and we can retain same code for loading resources
     * within IDE and .jar
     */
    private static final Config instance = new Config();

    /* APP CONSTANTS */
    public static final int BLOCK_SIZE = 40;

    /**
     * Default app width
     */
    public static final int APP_W = 32 * BLOCK_SIZE;

    /**
     * Default app height
     */
    public static final int APP_H = 18 * BLOCK_SIZE;

    /**
     * How many frames equal to a second
     * i.e. FPS
     */
    public static final int SECOND = 60;
    public static final float TIME_STEP = 1.0f / SECOND;

    public static Font FONT = Font.font(18);

    /**
     * A relative path from source directory
     * to resources
     */
    public static final String RESOURCES_ROOT = "/res/";
    public static final String IMAGES_ROOT = RESOURCES_ROOT + "images/";
    public static final String AUDIO_ROOT = RESOURCES_ROOT + "audio/";
    public static final String LEVELS_ROOT = RESOURCES_ROOT + "levels/";
    public static final String FONTS_ROOT = RESOURCES_ROOT + "fonts/";

    /* GAMEPLAY CONSTANTS */
    public static final int SCORE_COIN = 100;
    public static final int SCORE_POWERUP = 500;

    public static final Vec2 DEFAULT_GRAVITY = new Vec2(0, -10);
    public static final float MAX_PLAYER_SPEED = 10.0f;
    public static final float MAX_BULLET_TICKS = 1000.0f;

    public static final int MAX_LEVELS = 2;

    /* USER PREFERENCES (MODIFIABLE) */
    // TODO: persistent storage
    public static final class Properties {
        // TODO: move props here
    }
    /**
     * Current app width
     */
    public static final SimpleIntegerProperty appWidth = new SimpleIntegerProperty(APP_W);

    /**
     * Current app height
     */
    public static final SimpleIntegerProperty appHeight = new SimpleIntegerProperty(APP_H);

    /**
     * Ratio of current screen size to default
     */
    public static final SimpleDoubleProperty resolutionScale = new SimpleDoubleProperty(1.0);

    public static final SimpleDoubleProperty volume = new SimpleDoubleProperty(0.5);

    public static int getHalfWidth() {
        return appWidth.get() / 2;
    }

    public static void init() {
        try {
            appWidth.bind(resolutionScale.multiply(APP_W));
            appHeight.bind(resolutionScale.multiply(APP_H));

            Fonts.loadAll();
            Images.loadAll();
            Audio.loadAll();
            Text.loadAll();
        }
        catch (Exception e) {
            // shouldn't happen unless someone's tampering with the jar
            System.out.println("Couldn't load game resource: " + e.getMessage());
            System.out.println("Game will now exit");
            System.exit(0);
        }
    }

    public static final class Fonts {
        public static Font LOGO;

        private static Font loadFont(String path, double size) throws Exception {
            try (InputStream is = instance.getClass().getResourceAsStream(FONTS_ROOT + path)) {
                return Font.loadFont(is, size);
            }
        }

        private static void loadAll() throws Exception {
            LOGO = loadFont("spacebar.ttf", 72);
        }
    }

    public static final class Images {
        public static Image PLAYER;
        public static Image ENEMY;
        public static Image PLATFORM;
        public static Image EXPLOSION;
        public static Image SPIKE;
        public static Image COIN;
        public static Image STONE;
        public static Image POWERUP;

        private static Image loadImage(String path) throws Exception {
            try (InputStream is = instance.getClass().getResourceAsStream(IMAGES_ROOT + path)) {
                return new Image(is);
            }
        }

        private static void loadAll() throws Exception {
            PLAYER = loadImage("player1.png");
            ENEMY = loadImage("enemy3.png");
            PLATFORM = loadImage("platform.png");
            EXPLOSION = loadImage("explosion.png");
            SPIKE = loadImage("spike.png");
            COIN = loadImage("coin.png");
            STONE = loadImage("stone.png");
            POWERUP = loadImage("powerup.png");
        }
    }

    public static final class Audio {

        public static AudioClip EXPLOSION;
        public static AudioClip COIN;
        public static AudioClip POWERUP;

        public static Media testMedia;

        public static MediaPlayer mediaPlayer;

        public static void test() {
            testMedia = new Media(instance.getClass().getResource(AUDIO_ROOT + "test.mp3").toExternalForm());
            mediaPlayer = new MediaPlayer(testMedia);
            mediaPlayer.volumeProperty().bind(volume);
            mediaPlayer.setAutoPlay(true);
        }

        private static AudioClip loadAudio(String path) throws Exception {
            AudioClip clip = new AudioClip(instance.getClass().getResource(AUDIO_ROOT + path).toExternalForm());
            clip.volumeProperty().bind(volume);
            return clip;
        }

        private static void loadAll() throws Exception {
            EXPLOSION = loadAudio("explosion.wav");
            COIN = loadAudio("coin.wav");
            POWERUP = loadAudio("powerup.wav");
        }
    }

    public static final class Text {
        public static final ArrayList< List<String> > LEVEL_DATA = new ArrayList< List<String> >();

        //        public static List<String> LEVEL0;
        //        public static List<String> LEVEL1;

        private static List<String> loadText(String path) throws Exception {
            ArrayList<String> lines = new ArrayList<String>();

            try (InputStream is = instance.getClass().getResourceAsStream(path);
                    BufferedReader bf = new BufferedReader(new InputStreamReader(is))) {

                String line = "";
                while ((line = bf.readLine()) != null)
                    lines.add(line);

                return lines;
            }
        }

        private static void loadAll() throws Exception {
            for (int i = 0; i < MAX_LEVELS; i++) {
                LEVEL_DATA.add(loadText(LEVELS_ROOT + "level" + i + ".txt"));
            }
        }
    }
}
