package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UI;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NCCApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Ninja Card Combat");
        settings.setVersion("0.1");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        for (int i = 0; i < 5; i++) {
            Entity e = getRandomCard();
            getGameWorld().addEntity(e);
        }

        Entity player = new Entity();
        player.addControl(new CharacterControl());

//        getGameScene().getRoot().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
//            System.out.println(event);
//        });
    }

    @Override
    protected void initUI() {
        UI ui = getAssetLoader().loadUI("ui_main.fxml", new GameController(getGameWorld()));
        getGameScene().addUI(ui);
    }

    private StackPane buildFrame() {
        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER);

        Rectangle border = new Rectangle(getWidth() / 10, getHeight() / 4);
        border.setFill(null);
        border.setStroke(Color.BLACK);

        pane.getChildren().addAll(border);
        return pane;
    }

    private Entity getRandomCard() {
        Entity entity = new Entity();
        entity.addComponent(new AttackComponent(new Random().nextInt(10)));
        entity.addComponent(new DefenseComponent(new Random().nextInt(10)));

        return entity;
    }

    private EntityView getView(Entity e) {
        Text text = new Text();
        text.textProperty().bind(e.getComponent(AttackComponent.class).get().valueProperty().asString("Attack: [%d]\n")
                .concat(e.getComponent(DefenseComponent.class).get().valueProperty().asString("Defense: [%d]")));

        EntityView view = new EntityView();
        view.addNode(text);

        return view;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
