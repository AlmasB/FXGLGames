package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.EntityWorldListener;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.ui.UIController;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameController implements UIController {

    @FXML
    private VBox vboxCardsNeutral;
    @FXML
    private VBox vboxCardsEarth;
    @FXML
    private VBox vboxCardsFire;
    @FXML
    private VBox vboxCardsWater;
    @FXML
    private VBox vboxCardsAir;
    @FXML
    private VBox vboxCardsLightning;

    @FXML
    private Label labelNeutral;
    @FXML
    private Label labelEarth;
    @FXML
    private Label labelFire;
    @FXML
    private Label labelWater;
    @FXML
    private Label labelAir;
    @FXML
    private Label labelLightning;

    @FXML
    private HBox hboxEnemyCards;
    @FXML
    private HBox hboxPlayerCards;

    private GameWorld world;

    public GameController(GameWorld world) {
        this.world = world;
    }

    CharacterControl control;

    public void init() {
        Entity player = new Entity();
        control = new CharacterControl();
        labelAir.textProperty().bind(control.manaAirProperty().asString("Air: [%d]"));
        labelNeutral.textProperty().bind(control.manaNeutralProperty().asString("Neutral: [%d]"));
        labelEarth.textProperty().bind(control.manaEarthProperty().asString("Earth: [%d]"));
        labelFire.textProperty().bind(control.manaFireProperty().asString("Fire: [%d]"));
        labelLightning.textProperty().bind(control.manaLightningProperty().asString("Lightning: [%d]"));
        labelWater.textProperty().bind(control.manaWaterProperty().asString("Water: [%d]"));

        control.getCards().addListener((ListChangeListener<? super Entity>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    hboxPlayerCards.getChildren().addAll(c.getAddedSubList().stream().map(e -> getView(e)).collect(Collectors.toList()));
                }
            }
        });

        world.addWorldListener(new EntityWorldListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                vboxCardsNeutral.getChildren().add(getView(entity));
                vboxCardsEarth.getChildren().add(getView(entity));
                vboxCardsFire.getChildren().add(getView(entity));
                vboxCardsWater.getChildren().add(getView(entity));
                vboxCardsAir.getChildren().add(getView(entity));
                vboxCardsLightning.getChildren().add(getView(entity));
            }

            @Override
            public void onEntityRemoved(Entity entity) {

            }

            @Override
            public void onWorldUpdate(double tpf) {

            }

            @Override
            public void onWorldReset() {

            }
        });
    }

    private EntityView getView(Entity e) {
        Text text = new Text();
        text.textProperty().bind(e.getComponent(AttackComponent.class).get().valueProperty().asString("Attack: [%d]\n")
                .concat(e.getComponent(DefenseComponent.class).get().valueProperty().asString("Defense: [%d]")));

        StackPane pane = new StackPane();
        Rectangle border = new Rectangle(80, 80);
        border.setFill(Color.AQUA);
        border.setStroke(Color.BLUE);

        pane.getChildren().addAll(border, text);

        EntityView view = new EntityView();
        view.addNode(pane);


        view.setOnMouseClicked(event -> {
            control.getCards().add(e);
        });

        return view;
    }
}
