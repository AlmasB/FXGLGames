package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxglgames.ncc.NCCType.ENEMY_CARD;
import static com.almasb.fxglgames.ncc.NCCType.PLAYER_CARD;

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
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
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
            spawn("card", new SpawnData(100 + i*250, 50).put("type", ENEMY_CARD));
            spawn("card", new SpawnData(100 + i*250, 450).put("type", PLAYER_CARD));
        }
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

                if (card2.isAlive()) {
                    attack(cardEntity, cardEntity2);
                } else {
                    enemyCards.stream().filter(e -> e.getComponent(CardComponent.class).isAlive()).findAny().ifPresent(c -> {
                        attack(cardEntity, c);
                    });
                }
            }
        }

        // TODO: remove duplicate

        for (int i = 0; i < 5; i++) {
            Entity cardEntity = enemyCards.get(i);
            var card = cardEntity.getComponent(CardComponent.class);

            if (card.isAlive()) {
                Entity cardEntity2 = playerCards.get(i);
                var card2 = cardEntity2.getComponent(CardComponent.class);

                if (card2.isAlive()) {
                    attack(cardEntity, cardEntity2);
                } else {
                    playerCards.stream().filter(e -> e.getComponent(CardComponent.class).isAlive()).findAny().ifPresent(c -> {
                        attack(cardEntity, c);
                    });
                }
            }
        }

        if (!isAlive(playerCards)) {
            gameOver("You lose");
        } else if (!isAlive(enemyCards)) {
            gameOver("You win");
        }
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

    private void gameOver(String message) {
        getDisplay().showMessageBox(message, getGameController()::startNewGame);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
