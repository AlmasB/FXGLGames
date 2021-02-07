package com.almasb.fxglgames.geowars.menu;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.scene.Scene;
import com.almasb.fxglgames.geowars.service.HighScoreService;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.Bloom;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.geowars.Config.SAVE_FILE_NAME;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GeoWarsMainMenu extends FXGLMenu {

    private VBox scoresRoot = new VBox(10);
    private Node highScores;

    public GeoWarsMainMenu() {
        super(MenuType.MAIN_MENU);

        getContentRoot().getChildren().setAll(new Rectangle(getAppWidth(), getAppHeight()));

        var title = getUIFactoryService().newText(getSettings().getTitle(), Color.WHITE, 46.0);
        title.setStroke(Color.WHITESMOKE);
        title.setStrokeWidth(1.5);
        title.setEffect(new Bloom(0.6));
        centerTextBind(title, getAppWidth() / 2.0, 200);

        var version = getUIFactoryService().newText(getSettings().getVersion(), Color.WHITE, 22.0);
        centerTextBind(version, getAppWidth() / 2.0, 220);

        getContentRoot().getChildren().addAll(title, version);

        var color = Color.DARKBLUE;

        var blocks = new ArrayList<ColorBlock>();

        var blockStartX = getAppWidth() / 2.0 - 380;

        for (int i = 0; i < 15; i++) {
            var block = new ColorBlock(40, color);
            block.setTranslateX(blockStartX + i*50);
            block.setTranslateY(100);

            blocks.add(block);
            getContentRoot().getChildren().add(block);
        }

        for (int i = 0; i < 15; i++) {
            var block = new ColorBlock(40, color);
            block.setTranslateX(blockStartX + i*50);
            block.setTranslateY(220);

            blocks.add(block);
            getContentRoot().getChildren().add(block);
        }

        for (int i = 0; i < blocks.size(); i++) {
            var block = blocks.get(i);

            animationBuilder()
                    .delay(Duration.seconds(i * 0.05))
                    .duration(Duration.seconds(0.5))
                    .repeatInfinitely()
                    .autoReverse(true)
                    .animate(block.fillProperty())
                    .from(color)
                    .to(color.brighter().brighter())
                    .buildAndPlay(this);
        }

        var menuBox = new VBox(
                5,
                new MenuButton("New Game", () -> fireNewGame()),
                new MenuButton("High Scores", () -> toggleHighScores()),
                new MenuButton("Credits", () -> showCredits()),
                new MenuButton("Exit", () -> fireExit())
        );
        menuBox.setAlignment(Pos.TOP_CENTER);

        menuBox.setTranslateX(getAppWidth() / 2.0 - 125);
        menuBox.setTranslateY(getAppHeight() / 2.0 + 125);

        // useful for checking if nodes are properly centered
        var centeringLine = new Line(getAppWidth() / 2.0, 0, getAppWidth() / 2.0, getAppHeight());
        centeringLine.setStroke(Color.WHITE);

        scoresRoot.setPadding(new Insets(10));
        scoresRoot.setAlignment(Pos.TOP_LEFT);

        StackPane hsRoot = new StackPane(new Rectangle(450, 250, Color.color(0, 0, 0.2, 0.8)), scoresRoot);
        hsRoot.setAlignment(Pos.TOP_CENTER);
        hsRoot.setCache(true);
        hsRoot.setCacheHint(CacheHint.SPEED);
        hsRoot.setTranslateX(getAppWidth());
        hsRoot.setTranslateY(menuBox.getTranslateY());

        highScores = hsRoot;

        getContentRoot().getChildren().addAll(menuBox, hsRoot);
    }

    private void showCredits() {
        getDialogService().showMessageBox("Sprite assets: https://gisha.itch.io/spaceships-asset-pack?download\n" +
                "\n" +
                "Background assets: https://dinvstudio.itch.io/dynamic-space-background-lite-free\n" +
                "\n" +
                "BGM: Music by Eric Matyas\n" +
                "     \n" +
                "     www.soundimage.org");
    }

    private boolean isLoadedScore = false;

    @Override
    public void onCreate() {
        if (isLoadedScore)
            return;

        getService(HighScoreService.class).setNumScoresToKeep(5);

        getSaveLoadService().readAndLoadTask(SAVE_FILE_NAME).run();
        isLoadedScore = true;

        updateHighScores();
    }

    @Override
    public void onEnteredFrom(Scene prevState) {
        updateHighScores();
    }

    private void updateHighScores() {
        scoresRoot.getChildren().clear();

        HighScoreService highScoreService = getService(HighScoreService.class);
        highScoreService.getHighScores().forEach(data -> {
            var hsText = getUIFactoryService().newText(data.getTag() + ": " + data.getScore(), Color.WHITE, 32.0);

            scoresRoot.getChildren().add(hsText);
        });
    }

    private void toggleHighScores() {
        animationBuilder(this)
                .duration(Duration.seconds(0.66))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(highScores)
                .from(new Point2D(getAppWidth(), highScores.getTranslateY()))
                .to(new Point2D(getAppWidth() - 450, highScores.getTranslateY()))
                .buildAndPlay();
    }

    private static class MenuButton extends Parent {
        MenuButton(String name, Runnable action) {
            var text = getUIFactoryService().newText(name, Color.WHITE, 36.0);
            text.setStrokeWidth(1.5);
            text.strokeProperty().bind(text.fillProperty());

            text.fillProperty().bind(
                    Bindings.when(hoverProperty())
                            .then(Color.BLUE)
                            .otherwise(Color.WHITE)
            );

            setOnMouseClicked(e -> action.run());

            setPickOnBounds(true);

            getChildren().add(text);
        }
    }
}
