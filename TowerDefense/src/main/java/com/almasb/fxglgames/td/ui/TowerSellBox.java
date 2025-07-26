package com.almasb.fxglgames.td.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxglgames.td.TowerDefenseApp;
import com.almasb.fxglgames.td.ui.scene.MenuButton;
import javafx.scene.layout.HBox;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class TowerSellBox extends HBox {

    private Entity cell;

    public TowerSellBox() {
        setSpacing(5);

        var btn = new SellIcon();
        btn.setOnMouseClicked(e -> {
            if (cell != null) {
                FXGL.<TowerDefenseApp>getAppCast().onTowerSold(cell);
            }
        });

        getChildren().add(btn);
    }

    public void setCell(Entity cell) {
        this.cell = cell;
    }

    public Entity getCell() {
        return cell;
    }
}
