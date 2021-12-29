package com.almasb.fxglgames.td.ui.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.ui.FXGLScrollPane;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxglgames.td.TowerDefenseFactory;
import com.almasb.fxglgames.td.data.LevelData;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseMainMenu extends FXGLMenu {

    private Pane contentBox = new Pane();

    private Map<String, Image> mapPreviews = new HashMap<>();

    private MapPreview mapPreview = new MapPreview(getAppWidth() / 2, getAppHeight() / 2);

    public TowerDefenseMainMenu() {
        super(MenuType.MAIN_MENU);

        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.DARKGRAY);

        var gradient = new LinearGradient(
                0.5, 0, 0.5, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.1, Color.color(0.1, 0.35, 0.2, 0.15)),
                new Stop(0.5, Color.color(0.1, 0.65, 0.2, 0.95)),
                new Stop(0.9, Color.color(0.1, 0.35, 0.2, 0.15))
        );

        var bgInnerLeft = new Rectangle(250, getAppHeight() + 2, gradient);
        bgInnerLeft.setStrokeWidth(1.5);
        bgInnerLeft.setStroke(Color.LIGHTGREEN);
        bgInnerLeft.setTranslateX(80);

        var bgInnerRight = new Rectangle(getAppWidth() - 80 - 250 - 80 - 80, getAppHeight() + 2, gradient);
        bgInnerRight.setStrokeWidth(1.5);
        bgInnerRight.setStroke(Color.LIGHTGREEN);
        bgInnerRight.setTranslateX(80 + bgInnerLeft.getWidth() + 80);

        var title = getUIFactoryService().newText(
                getSettings().getTitle() + " " + getSettings().getVersion(),
                Color.BLACK, FontType.MONO, 16.0
        );
        title.setTranslateX(bgInnerLeft.getTranslateX() + 5);
        title.setTranslateY(50);

        var version = getUIFactoryService().newText(getSettings().getVersion(), Color.ANTIQUEWHITE, FontType.MONO, 12.0);
        version.setTranslateX(bgInnerLeft.getTranslateX() + 10);
        version.setTranslateY(getAppHeight() - 5);

        // TODO: where to read this from?
        List<String> levelNames = List.of(
                "level2.json",
                "level1.json"
        );

        var listView = getUIFactoryService().newListView(FXCollections.observableList(levelNames));
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, levelName) -> {

            if (levelName != null)
                getExecutor().startAsync(() -> showLevelPreview(levelName));
        });

        listView.getSelectionModel().selectFirst();

        var scrollForListView = new FXGLScrollPane(listView);
        scrollForListView.setPrefHeight(getAppHeight() / 2.0);

        // content views

        var hbox = new HBox(20, scrollForListView, mapPreview);
        var btnStartLevel = new Button(" Play ");
        btnStartLevel.getStyleClass().add("play_button");
        btnStartLevel.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
        btnStartLevel.setTranslateX(bgInnerRight.getWidth() / 2 - 100);
        btnStartLevel.setTranslateY(getAppHeight() / 2.0 + 50);
        btnStartLevel.setOnAction(e -> {
            // TODO:
        });

        var menuBox = new VBox(-15,
                new MenuButton("Levels", () -> setContent(new Group(hbox, btnStartLevel))),
                new MenuButton("Runes", () -> showMessage("TODO: not implemented")),
                new MenuButton("Upgrades", () -> showMessage("TODO: not implemented")),
                new MenuButton("Settings", () -> showMessage("TODO: not implemented")),
                new MenuButton("Exit", () -> fireExit())
        );
        menuBox.setTranslateX(bgInnerLeft.getTranslateX() + 15);
        menuBox.setTranslateY(250);

        contentBox.setTranslateX(bgInnerRight.getTranslateX() + 15);
        contentBox.setTranslateY(220);

        getContentRoot().getChildren().addAll(
                bg,
                bgInnerLeft,
                bgInnerRight,
                menuBox,
                contentBox,
                //title,
                version
        );
    }

    private void setContent(Node view) {
        contentBox.getChildren().setAll(view);
    }

    private void showLevelPreview(String levelName) {
        Image image;

        if (mapPreviews.containsKey(levelName)) {
            image = mapPreviews.get(levelName);
        } else {

            var levelData = getAssetLoader().loadJSON("levels/" + levelName, LevelData.class).get();

            var scene = new GameSubScene(getAppWidth() / 2, getAppHeight() / 2);
            scene.getGameScene().getViewport().setZoom(0.5);
            scene.getGameWorld().addEntityFactory(new TowerDefenseFactory());

            var level = new TMXLevelLoader(true).load(getAssetLoader().getURL("/assets/levels/tmx/" + levelData.map()), scene.getGameWorld());

            scene.getGameWorld().setLevel(level);

            image = ImagesKt.toImage(scene.getContentRoot());

            mapPreviews.put(levelName, image);
        }

        getExecutor().startAsyncFX(() -> mapPreview.view.setImage(image));
    }

    private static class MapPreview extends Parent {
        ImageView view = new ImageView();

        MapPreview(int width, int height) {

            var bg = new Rectangle(width, height, Color.TRANSPARENT);
            bg.setArcWidth(15);
            bg.setArcHeight(15);
            bg.setStrokeType(StrokeType.OUTSIDE);
            bg.setStroke(Color.LIGHTGREY);
            bg.setStrokeWidth(2.5);

            getChildren().addAll(view, bg);
        }
    }

    private static class MenuButton extends Parent {

        MenuButton(String name, Runnable action) {
            var bg = new Polygon(
                    0.0, 0.0,
                    200.0, 0.0,
                    200.0, 25.0,
                    190.0, 40.0,
                    0.0, 40.0
            );
            bg.setStrokeWidth(2.5);
            bg.strokeProperty().bind(
                    Bindings.when(hoverProperty()).then(Color.GOLD).otherwise(Color.TRANSPARENT)
            );
            bg.fillProperty().bind(
                    Bindings.when(hoverProperty()).then(Color.DARKORANGE).otherwise(Color.TRANSPARENT)
            );

            var text = getUIFactoryService().newText(name, Color.WHITE, FontType.GAME, 26.0);
            text.setTranslateX(15);
            text.setTranslateY(28);
            text.fillProperty().bind(
                    Bindings.when(disableProperty())
                            .then(Color.GRAY)
                            .otherwise(
                                    Bindings.when(pressedProperty()).then(Color.YELLOWGREEN).otherwise(Color.WHITE)
                            )
            );

            setOnMouseClicked(e -> action.run());

            getChildren().addAll(bg, text);
        }
    }
}