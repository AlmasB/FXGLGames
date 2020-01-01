package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.ncc.Config.*;
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

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        getGameWorld().addEntityFactory(new NCCFactory());

        for (int i = 0; i < 5; i++) {
            spawn("card", new SpawnData(50 + i*(CARD_WIDTH + 25), 20).put("type", ENEMY_CARD));
            spawn("card", new SpawnData(50 + i*(CARD_WIDTH + 25), 400).put("type", PLAYER_CARD));
        }
    }

    @Override
    protected void initUI() {
        var text = getUIFactory().newText("PROOF OF CONCEPT", Color.BLACK, 44.0);

        addUINode(text, getAppWidth() / 2.0, getAppHeight() - 30);

        var btnNext = getUIFactory().newButton("Next Turn");
        btnNext.setOnAction(e -> nextTurn());

        //addUINode(btnNext, getAppWidth() - 250, getAppHeight() - 60);
    }

    private void nextTurn() {
        var playerCards = byType(PLAYER_CARD);
        var enemyCards = byType(ENEMY_CARD);

        for (int i = 0; i < 5; i++) {
            Entity cardEntity = playerCards.get(i);
            var card = cardEntity.getComponent(CardComponent.class);

            if (card.isAlive()) {
                Entity cardEntity2 = enemyCards.get(i);
                var card2 = cardEntity2.getComponent(CardComponent.class);

                Entity targetEntity;

                if (card2.isAlive()) {
                    targetEntity = cardEntity2;
                } else {
                    targetEntity = enemyCards.stream()
                            .filter(e -> e.getComponent(CardComponent.class).isAlive())
                            .findAny()
                            .orElse(null);
                }

                // TODO: animations are delayed and start when targetEntity is alive
                // we still need to check targetEntity's hp during animation

                if (targetEntity != null) {
                    animationBuilder()
                            .delay(Duration.seconds(i))
                            .duration(Duration.seconds(0.5))
                            .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                            .repeat(2)
                            .autoReverse(true)
                            .translate(cardEntity)
                            .from(cardEntity.getPosition())
                            .to(cardEntity2.getPosition().add(0, CARD_HEIGHT / 2.0))
                            .buildAndPlay();

                    animationBuilder()
                            .delay(Duration.seconds(0.5 + i))
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

        // TODO: remove duplicate

//        for (int i = 0; i < 5; i++) {
//            Entity cardEntity = enemyCards.get(i);
//            var card = cardEntity.getComponent(CardComponent.class);
//
//            if (card.isAlive()) {
//                Entity cardEntity2 = playerCards.get(i);
//                var card2 = cardEntity2.getComponent(CardComponent.class);
//
//                if (card2.isAlive()) {
//                    attack(cardEntity, cardEntity2);
//                } else {
//                    playerCards.stream().filter(e -> e.getComponent(CardComponent.class).isAlive()).findAny().ifPresent(c -> {
//                        attack(cardEntity, c);
//                    });
//                }
//            }
//        }
//
//        if (!isAlive(playerCards)) {
//            gameOver("You lose");
//        } else if (!isAlive(enemyCards)) {
//            gameOver("You win");
//        }
    }

    private void attack(Entity e1, Entity e2) {
        int atk = e1.getComponent(CardComponent.class).getAtk();
        int def = e2.getComponent(CardComponent.class).getDef();

        int hp = e2.getComponent(CardComponent.class).getHp() - (atk - def);
        e2.getComponent(CardComponent.class).setHp(hp);
    }

    private boolean isAlive(List<Entity> cards) {
        return cards.stream()
                .map(e -> e.getComponent(CardComponent.class))
                .anyMatch(CardComponent::isAlive);
    }

    private void onSkill(Entity user, Skill skill, List<Entity> targets) {

    }

    private void gameOver(String message) {
        getDisplay().showMessageBox(message, getGameController()::startNewGame);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
