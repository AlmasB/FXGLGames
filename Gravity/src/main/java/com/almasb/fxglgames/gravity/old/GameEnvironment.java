package com.almasb.fxglgames.gravity.old;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.transform.Scale;

import com.almasb.fxgl.physics.box2d.callbacks.ContactImpulse;
import com.almasb.fxgl.physics.box2d.callbacks.ContactListener;
import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.World;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;

public abstract class GameEnvironment extends Application {
    /**
     * Physics simulation world
     */
    private static World world;

    public static World getWorld() {
        return world;
    }

    public static World resetWorld() {
        // clean up first
        if (world != null) {
//            Body body = world.getBodyList();
//            while (body != null) {
//                Body nextBody = body.getNext();
//                world.destroyBody(body);
//                body = nextBody;
//            }
        }

        world = new World(Config.DEFAULT_GRAVITY);
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                GameObject obj1 = (GameObject) contact.getFixtureA().getBody().getUserData();
                GameObject obj2 = (GameObject) contact.getFixtureB().getBody().getUserData();

                obj1.onCollide(obj2);
                obj2.onCollide(obj1);
            }

            @Override
            public void endContact(Contact contact) {}
            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}
            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        return world;
    }

    /**
     * Player instance
     */
    private static Player player;

    public static Player getPlayer() {
        return player;
    }

    public static void setPlayer(Player p) {
        player = p;
    }

    /**
     * Stores objects for display
     * Elements in gameRoot are affected by camera control
     */
    protected static final Group LEVEL_ROOT = new Group();

    /**
     * Elements in uiRoot are always on the screen and do not move
     */
    protected static final Group UI_ROOT = new Group();

    /**
     * Stores all game objects of the current level
     * for update
     *
     * This is practically a copy list of {@link #LEVEL_ROOT} that
     * contains same references but having this typechecked saves
     * from constant typecast during the loop
     */
    protected static final ObservableList<GameObject> LEVEL_OBJECTS = FXCollections.<GameObject>observableArrayList();

    /**
     * Stores objects to be added to the game during next loop cycle
     */
    protected static final ArrayList<GameObject> tmpList = new ArrayList<GameObject>();

    /**
     * Used to dynamically add objects to already
     * running game
     *
     * This registers the object for game updates,
     * adds to the game view
     *
     * @param obj
     *              object to add to game
     */
    public static void addObject(GameObject obj) {
        tmpList.add(obj);
    }

    public static final SimpleBooleanProperty levelConditionsMet = new SimpleBooleanProperty();

    /**
     * User actions
     */
    protected static final int UP = 0,
            DOWN = 1,
            LEFT = 2,
            RIGHT = 3,
            GRAVITY = 4,
            PULL = 5,
            PUSH = 6,
            LAST = 7;

    // TODO: add observable binding -> methods are triggered automatically
    private HashMap<Integer, KeyCode> keyBindings = new HashMap<Integer, KeyCode>();
    // TODO: make enums
    protected boolean[] keys = new boolean[LAST];
    protected int[] skillCosts = new int[LAST];

    protected abstract Parent createContent();
    protected abstract void nextLevel();
    protected abstract void update();
    protected abstract void handleKeyPressed(KeyEvent event);
    protected abstract void handleKeyReleased(KeyEvent event);

    protected AnimationTimer timer;

    private boolean gameStarted = false;

    enum Debug {
        GAME, MENU, INTRO
    }

    private Debug debug = Debug.INTRO;

    @Override
    public void start(Stage primaryStage) throws Exception {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                if (levelConditionsMet.get()) {
                    nextLevel();
                    levelConditionsMet.set(false);
                }
            }
        };

        skillCosts[UP] = 2;
        skillCosts[GRAVITY] = 5;
        skillCosts[PULL] = 120;
        skillCosts[PUSH] = 120;

        keyBindings.put(UP, KeyCode.W);
        keyBindings.put(DOWN, KeyCode.S);
        keyBindings.put(RIGHT, KeyCode.D);
        keyBindings.put(LEFT, KeyCode.A);
        keyBindings.put(GRAVITY, KeyCode.G);
        keyBindings.put(PULL, KeyCode.Q);
        keyBindings.put(PUSH, KeyCode.E);

        Menu menu = new Menu();

        Scene sceneMenu = new Scene(menu);
        Scene sceneGame = new Scene(createContent());

        sceneMenu.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && gameStarted) {
                primaryStage.setScene(sceneGame);
                timer.start();
            }
        });

        sceneGame.setOnKeyPressed(event -> {
            for (Integer action : keyBindings.keySet()) {
                if (keyBindings.get(action) == event.getCode()) {
                    keys[action] = true;
                    break;
                }
            }

            if (event.getCode() == KeyCode.ESCAPE) {
                timer.stop();
                primaryStage.setScene(sceneMenu);
                return;
            }

            handleKeyPressed(event);
        });
        sceneGame.setOnKeyReleased(event -> {
            for (Integer action : keyBindings.keySet()) {
                if (keyBindings.get(action) == event.getCode()) {
                    keys[action] = false;
                    break;
                }
            }

            handleKeyReleased(event);
        });

        menu.newGameEvent.addListener((obs, old, newValue) -> {
            if (newValue.booleanValue()) {
                gameStarted = true;
                primaryStage.setScene(sceneGame);
                timer.start();
                Config.Audio.test();
            }
        });

        if (debug == Debug.INTRO) {
            Intro intro = new Intro();
            Scene sceneIntro = new Scene(intro);
            intro.setOnFinished(event -> {
                primaryStage.setScene(sceneMenu);
            });
            primaryStage.setScene(sceneIntro);
        }
        else if (debug == Debug.GAME) {
            gameStarted = true;
            primaryStage.setScene(sceneGame);
            timer.start();
            Config.Audio.test();
        }
        else if (debug == Debug.MENU) {
            primaryStage.setScene(sceneMenu);
        }

        LEVEL_ROOT.getTransforms().add(new Scale(1, 1));
        Config.resolutionScale.addListener((obs, old, newValue) -> {
            primaryStage.setWidth((int)(Config.APP_W * newValue.floatValue()));
            primaryStage.setHeight((int)(Config.APP_H * newValue.floatValue()));
            primaryStage.centerOnScreen();

            LEVEL_ROOT.getTransforms().set(0, new Scale(newValue.floatValue(), newValue.floatValue()));
        });

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setWidth(Config.APP_W);
        primaryStage.setHeight(Config.APP_H);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Gravity");
        primaryStage.show();
    }
}
