package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.ui.FXGLScrollPane;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.ncc.Config.*;
import static com.almasb.fxglgames.ncc.NCCFactory.DECKS;
import static com.almasb.fxglgames.ncc.NCCType.ENEMY_CARD;
import static com.almasb.fxglgames.ncc.NCCType.PLAYER_CARD;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class NCCApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL Ninja Card Combat");
        settings.setVersion("0.1");
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.ENTER, "Next Turn", this::nextTurn);
    }

    private Entity selectedCard;

    private Rectangle highlightRect;

    private int playerCardsSelected;

    @Override
    protected void initGame() {
        highlightRect = new Rectangle(CARD_WIDTH, CARD_HEIGHT, Color.color(0.7, 0.9, 0.8, 0.5));
        playerCardsSelected = 0;

        entityBuilder()
                .view(texture("bg.png").darker().desaturate())
                .buildAndAttach();

        getGameWorld().addEntityFactory(new NCCFactory());

        var box = new HBox(25,
                DECKS.stream()
                        .flatMap(d -> d.getCards().stream())
                        .map(c -> getGameWorld().create("card", new SpawnData(0, 0).put("card", c)))
                        .map(e -> {
                            var view = e.getViewComponent().getParent();

                            view.setOnMouseClicked(event -> {
                                selectCard(e);
                            });

                            return view;
                        })
                        .collect(Collectors.toList())
                        .toArray(Node[]::new)
        );

        var deckView = new FXGLScrollPane(box);
        deckView.setMaxSize(APP_WIDTH * 0.75, CARD_HEIGHT + 50);
        deckView.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);




        // TODO: cleanup

        for (int i = 0; i < 5; i++) {
            spawn("cardPlaceholder", new SpawnData(50 + i*(CARD_WIDTH + 25), 40 + 30).put("isPlayer", false));

            var playerPlaceholder = spawn("cardPlaceholder", new SpawnData(50 + i*(CARD_WIDTH + 25), 420 + 30).put("isPlayer", true));

            playerPlaceholder.getViewComponent().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                var btn = getUIFactoryService().newButton("SELECT");
                btn.setOnAction(event -> {
                    // done selecting card from dialog
                    selectedCard.getViewComponent().removeChild(highlightRect);
                    box.getChildren().remove(selectedCard.getViewComponent().getParent());

                    selectedCard.getViewComponent().getParent().setLayoutX(0);
                    selectedCard.getViewComponent().getParent().setLayoutY(0);
                    selectedCard.getViewComponent().getParent().setOnMouseClicked(null);
                    selectedCard.getTransformComponent().setZ(100);

                    selectedCard.setPosition(playerPlaceholder.getPosition().subtract(0, 0));
                    selectedCard.setType(PLAYER_CARD);

                    // TODO: apply costs e.g. sp hp
                    // add skill click handlers
                    selectedCard.getComponent(CardViewComponent.class).getSkillView1().ifPresent(skillView -> {
                        skillView.setOnMouseClicked(ev -> {
                            //SkillHandlers.handle(selectedCard, skillView.getSkill());
                        });
                    });

                    selectedCard.getComponent(CardViewComponent.class).getSkillView2().ifPresent(skillView -> {
                        var skill = skillView.getSkill();

                        skillView.setOnMouseClicked(ev -> {
                            if (skill.getTargetType() == TargetType.ALL_ALLIES) {
                                // TODO: generalize depending on who cast: player or enemy
                                SkillHandlers.handle(selectedCard, skill, byType(PLAYER_CARD));
                            }
                        });
                    });

                    getGameWorld().addEntity(selectedCard);

                    playerPlaceholder.removeFromWorld();

                    playerCardsSelected++;

                    selectCardAI();
                });

                getDialogService().showBox("Card Select", deckView, btn);
            });
        }
    }

    private void selectCard(Entity card) {
        if (selectedCard != null) {
            selectedCard.getViewComponent().removeChild(highlightRect);
        }

        selectedCard = card;
        selectedCard.getViewComponent().addChild(highlightRect);
    }

    private void selectCardAI() {
        if (playerCardsSelected == 1) {
            placeEnemyCards(2);

        } else if (playerCardsSelected == 3) {
            placeEnemyCards(2);

        } else if (playerCardsSelected == 5) {
            placeEnemyCards(1);
        }
    }

    private void placeEnemyCards(int num) {
        getGameWorld().getEntitiesFiltered(e -> e.getPropertyOptional("isPlayer").isPresent() && !e.getBoolean("isPlayer"))
                .stream()
                .limit(num)
                .forEach(e -> {
                    // this is where AI picks the cards based on player cards
                    spawn("card", new SpawnData(e.getPosition()).put("type", ENEMY_CARD).put("card", getRandomCard()));

                    e.removeFromWorld();
                });
    }

    @Override
    protected void initUI() {
        var btnNext = getUIFactoryService().newButton("Next Turn");
        btnNext.setOnAction(e -> nextTurn());

        addUINode(btnNext, getAppWidth() - 250, getAppHeight() - 40);
    }

    private void nextTurn() {
        var playerCards = byType(PLAYER_CARD);
        var enemyCards = byType(ENEMY_CARD);

        runTurn(playerCards, enemyCards);

        runOnce(() -> runTurn(enemyCards, playerCards), Duration.seconds(7));

//        if (!isAlive(playerCards)) {
//            gameOver("You lose");
//        } else if (!isAlive(enemyCards)) {
//            gameOver("You win");
//        }
    }

    private void runTurn(List<Entity> atkCards, List<Entity> defCards) {
        for (int i = 0; i < 5; i++) {
            Entity cardEntity = atkCards.get(i);
            var card = cardEntity.getComponent(CardComponent.class);

            if (card.isAlive()) {
                Entity cardEntity2 = defCards.get(i);
                var card2 = cardEntity2.getComponent(CardComponent.class);

                Entity targetEntity;

                if (card2.isAlive()) {
                    targetEntity = cardEntity2;
                } else {
                    targetEntity = defCards.stream()
                            .filter(e -> e.getComponent(CardComponent.class).isAlive())
                            .findAny()
                            .orElse(null);
                }

                // TODO: animations are delayed and start when targetEntity is alive
                // we still need to check targetEntity's hp during animation

                // TODO: add next turn button and disable while turn is being executed

                if (targetEntity != null) {
                    animationBuilder()
                            .delay(Duration.seconds(i))
                            .duration(Duration.seconds(0.3))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .repeat(2)
                            .autoReverse(true)
                            .translate(cardEntity)
                            .alongPath(new CubicCurve(
                                    cardEntity.getX(), cardEntity.getY(),
                                    cardEntity.getX() - 100, (cardEntity.getY() + cardEntity2.getY()) / 3,
                                    cardEntity.getX() - 150, (cardEntity.getY() + cardEntity2.getY()) / 3 * 2,
                                    cardEntity2.getX(), cardEntity2.getY()
                            ))
//                            .from(cardEntity.getPosition())
//                            .to(cardEntity2.getPosition().add(0, 0))
                            .buildAndPlay();

                    animationBuilder()
                            .delay(Duration.seconds(0.2 + i))
                            .duration(Duration.seconds(0.1))
                            .interpolator(Interpolators.ELASTIC.EASE_OUT())
                            .repeat(4)
                            .autoReverse(true)
                            .onFinished(() -> attack(cardEntity, targetEntity))
                            .translate(targetEntity)
                            .from(targetEntity.getPosition())
                            .to(targetEntity.getPosition().add(2.5, 0))
                            .buildAndPlay();
                }
            }
        }
    }

    private void attack(Entity e1, Entity e2) {
        int atk = e1.getComponent(CardComponent.class).getAtk();
        int def = e2.getComponent(CardComponent.class).getDef();

        var damage = Math.max(0, atk - def);

        e2.getComponent(CardComponent.class).getHp().damage(damage);
    }

    private boolean isAlive(List<Entity> cards) {
        return cards.stream()
                .map(e -> e.getComponent(CardComponent.class))
                .anyMatch(CardComponent::isAlive);
    }

    private void gameOver(String message) {
        getDialogService().showMessageBox(message, getGameController()::startNewGame);
    }

    private Card getRandomCard() {
        return FXGLMath.random(DECKS)
                .map(Deck::getCards)
                .flatMap(FXGLMath::random)
                .get();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
