package com.almasb.fxglgames.td.ui.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.GameSubScene;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxglgames.td.TowerDefenseFactory;
import com.almasb.fxglgames.td.data.LevelData;
import javafx.collections.FXCollections;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerDefenseMainMenu extends FXGLMenu {

    private Map<String, Image> mapPreviews = new HashMap<>();

    private ImageView mapPreview = new ImageView();

    public TowerDefenseMainMenu() {
        super(MenuType.MAIN_MENU);

        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.DARKGREEN);

        var title = getUIFactoryService().newText(getSettings().getTitle() + "\nUI is work-in-progress...", Color.WHITE, 24.0);
        centerText(title, getAppWidth() / 2.0, 40);

        // TODO: where to read this from?
        List<String> levelNames = List.of(
                "level2.json",
                "level1.json"
        );

        var listView = getUIFactoryService().newListView(FXCollections.observableList(levelNames));
        listView.setTranslateX(250);
        listView.setTranslateY(150);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, levelName) -> {

            if (levelName != null)
                getExecutor().startAsync(() -> showLevelPreview(levelName));
        });

        listView.getSelectionModel().selectFirst();

        mapPreview.setTranslateX(500);
        mapPreview.setTranslateY(150);

        var btnSelectLevel = getUIFactoryService().newButton("Select Level");
        btnSelectLevel.setOnAction(e -> {
            // TODO: start game with new level
        });

        var btnExit = getUIFactoryService().newButton("Exit");
        btnExit.setOnAction(e -> fireExit());

        var menuBox = new VBox(btnSelectLevel, btnExit);
        menuBox.setTranslateX(50);
        menuBox.setTranslateY(150);

        getContentRoot().getChildren().addAll(
                bg,
                title,
                menuBox,
                listView,
                mapPreview
        );
    }

    private void showLevelPreview(String levelName) {
        Image image;

        if (mapPreviews.containsKey(levelName)) {
            image = mapPreviews.get(levelName);
        } else {

            var levelData = getAssetLoader().loadJSON("levels/" + levelName, LevelData.class).get();

            var scene = new LevelPreviewSubScene(getAppWidth() / 2, getAppHeight() / 2);
            scene.getGameWorld().addEntityFactory(new TowerDefenseFactory());

            var level = new TMXLevelLoader().load(getAssetLoader().getURL("/assets/levels/tmx/" + levelData.map()), scene.getGameWorld());

            scene.getGameWorld().setLevel(level);

            image = ImagesKt.toImage(scene.getContentRoot());

            mapPreviews.put(levelName, image);
        }

        getExecutor().startAsyncFX(() -> mapPreview.setImage(image));
    }

    private static class LevelPreviewSubScene extends GameSubScene {

        public LevelPreviewSubScene(int width, int height) {
            super(width, height);

            this.getGameScene().getViewport().setZoom(0.5);
        }

        @Override
        public boolean isAllowConcurrency() {
            return true;
        }
    }
}
