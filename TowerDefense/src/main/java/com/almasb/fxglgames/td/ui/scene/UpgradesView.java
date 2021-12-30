package com.almasb.fxglgames.td.ui.scene;

import com.almasb.fxglgames.td.data.TowerData;
import com.almasb.fxglgames.td.ui.TowerIcon;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getAssetLoader;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class UpgradesView extends Parent {

    public UpgradesView() {
        var box = new HBox(20);

        // TODO: remove duplicate

        List<String> towerNames = List.of(
                "tower1.json",
                "tower2.json",
                "tower3.json",
                "tower4.json",
                "tower5.json",
                "tower6.json"
        );

        var towerData = towerNames.stream()
                .map(name -> getAssetLoader().loadJSON("towers/" + name, TowerData.class).get())
                .toList();

        towerData.forEach(tower -> {
            box.getChildren().add(new TowerIcon(tower));
        });

        getChildren().add(box);
    }
}