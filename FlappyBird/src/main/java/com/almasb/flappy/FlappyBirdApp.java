package com.almasb.flappy;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGLListener;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FlappyBirdApp extends GameApplication {

    private PlayerControl playerControl;
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Flappy Bird Clone");
        settings.setVersion("0.1");
        settings.setShowFPS(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void preInit() {
        Music bgm = getAssetLoader().loadMusic("bgm.mp3");
        bgm.setCycleCount(Integer.MAX_VALUE);

        getAudioPlayer().playMusic(bgm);

        addFXGLListener(new FXGLListener() {
            @Override
            public void onPause() {}

            @Override
            public void onResume() {}

            @Override
            public void onReset() {}

            @Override
            public void onExit() {
                getAudioPlayer().stopMusic(bgm);
                bgm.dispose();
            }
        });
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.SPACE);

        getInput().addAction(new UserAction("Exit") {
            @Override
            protected void onActionBegin() {
                pause();
                exit();
            }
        }, KeyCode.L);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        color.setValue(Color.BLACK);

        initBackground();
        initPlayer();
    }

    private boolean requestNewGame = false;
    private boolean reset = false;

    private List<Particle> particles = new ArrayList<>();

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                if (reset)
                    return;

                Image image = getGameScene().getRoot().getScene().snapshot(null);

                PixelReader reader = image.getPixelReader();

                for (int y = 0; y < (int) image.getHeight(); y++) {
                    for (int x = 0; x < (int) image.getWidth(); x++) {
                        Particle p = new Particle(x, y, reader.getColor(x, y));
                        if (!p.color.equals(color.get() == Color.WHITE ? Color.BLACK : Color.WHITE))
                            particles.add(p);
                    }
                }

                System.out.println(particles.size());

                reset = true;
            }
        });
    }

    private static class Particle {
        private double x, y;
        private Color color;

        public Particle(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    @Override
    protected void initUI() {}

    double time = 0;

    @Override
    protected void onUpdate(double tpf) {
        GraphicsContext g = getGameScene().getGraphicsContext();

        if (!particles.isEmpty()) {
            g.setFill(color.getValue().invert());
            g.fillRect(0, 0, getWidth(), getHeight());
            
            time += tpf;

            if (time >= 3) {
                requestNewGame = true;
            }
        }

        for (Particle p : particles) {
            double vx = getWidth() / 2 - p.x;
            double vy = getHeight() / 2 - p.y;

            p.x += vx * (Math.random() - 0.5) * 0.01;
            p.y += vy * (Math.random() - 0.5) * 0.01;



            g.setFill(p.color);
            g.setGlobalAlpha(Math.max(1 - time / 3, 0));
            g.fillOval(p.x, p.y, 0.5, 0.5);
        }
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            time = 0;
            particles.clear();
            startNewGame();
        }

        if (reset) {
            getGameWorld().reset();
            reset = false;
        }
    }

    private void initBackground() {
        GameEntity bg = Entities.builder()
                .type(EntityType.BACKGROUND)
                .viewFromNode(new Rectangle(getWidth(), getHeight(), Color.WHITE))
                .with(new ColorChangingControl())
                .buildAndAttach(getGameWorld());

        bg.getPositionComponent().xProperty().bind(getGameScene().getViewport().xProperty());
        bg.getPositionComponent().yProperty().bind(getGameScene().getViewport().yProperty());
    }

    private void initPlayer() {
        playerControl = new PlayerControl();

        Texture view = getAssetLoader().loadTexture("player.png")
                .toStaticAnimatedTexture(2, Duration.seconds(0.33));

        GameEntity player = Entities.builder()
                .at(100, 100)
                .type(EntityType.PLAYER)
                .bbox(new HitBox("BODY", BoundingShape.box(70, 60)))
                .viewFromNode(view)
                .with(new CollidableComponent(true))
                .with(playerControl, new WallBuildingControl())
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, (int) getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 3, getHeight() / 2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
