package com.almasb.fxglgames.td.ui.scene;

import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RunesView extends Parent {

    public RunesView() {
        var gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                var frame = new RuneView("???");

                gridPane.add(frame, x, y);
            }
        }

        getChildren().add(gridPane);
    }

    private static class RuneView extends StackPane {
        RuneView(String name) {
            var bg = new Rectangle(100, 100, Color.ALICEBLUE);
            bg.setArcWidth(10);
            bg.setArcHeight(10);
            bg.setStrokeWidth(2.5);
            bg.setStroke(Color.BLACK);

            var text = new Text(name);
            text.setFill(Color.BLACK);

            getChildren().addAll(bg, text);
        }
    }
}
