package com.almasb.fxglgames.td.ui.scene;

import com.almasb.fxgl.ui.FontType;
import com.almasb.fxglgames.td.data.LevelData;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.List;
import java.util.function.Consumer;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getUIFactoryService;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LevelSelectionBox extends VBox {

    private ObjectProperty<MenuItem> selected = new SimpleObjectProperty<>();
    private ObjectProperty<LevelData> selectedLevel = new SimpleObjectProperty<>();

    public LevelSelectionBox(List<LevelData> levels) {
        super(-20);

        levels.forEach(data -> {
            MenuItem item = new MenuItem(data, data.name(), i -> select(i));

            getChildren().add(item);
        });

        var item = (MenuItem) getChildren().get(0);
        select(item);
    }

    public ObjectProperty<LevelData> selectedLevelProperty() {
        return selectedLevel;
    }

    public LevelData getSelectedLevel() {
        return selectedLevel.get();
    }

    private void select(MenuItem item) {
        if (selected.get() != null)
            selected.get().setSelected(false);

        selected.set(item);
        selectedLevel.set(item.data);

        selected.get().setSelected(true);
    }

    private static class MenuItem extends Parent {

        Polygon bg;
        boolean isSelected = false;

        LevelData data;

        MenuItem(LevelData data, String name, Consumer<MenuItem> action) {
            this.data = data;

            bg = new Polygon(
                    0.0, 0.0,
                    200.0, 0.0,
                    200.0, 25.0,
                    190.0, 40.0,
                    0.0, 40.0
            );
            bg.setStrokeWidth(2.5);

            var text = getUIFactoryService().newText(name, Color.WHITE, FontType.GAME, 26.0);
            text.setTranslateX(15);
            text.setTranslateY(28);
            text.fillProperty().bind(
                    Bindings.when(disableProperty())
                            .then(Color.GRAY)
                            .otherwise(
                                    Bindings.when(pressedProperty()).then(Color.YELLOWGREEN).otherwise(Color.WHITE)
                            )
            );

            setOnMouseClicked(e -> action.accept(this));

            getChildren().addAll(bg, text);

            setSelected(false);
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;

            bg.setFill(isSelected ? Color.GOLD : Color.TRANSPARENT);
            bg.setStroke(isSelected ? Color.DARKORANGE : Color.TRANSPARENT);
        }
    }
}
