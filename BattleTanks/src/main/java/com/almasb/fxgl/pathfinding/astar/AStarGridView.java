package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class AStarGridView extends Parent {

    private AStarGrid grid;

    public AStarGridView(AStarGrid grid, int cellWidth, int cellHeight) {
        this.grid = grid;

        // TODO: use Group for multiple layers to avoid multiple iterations
        grid.forEach(cell -> {
            if (cell.getState() == CellState.NOT_WALKABLE) {
                var rect = new Rectangle(cellWidth, cellHeight, Color.color(0.8, 0.0, 0.0, 0.75));
                rect.setTranslateX(cell.getX() * cellWidth);
                rect.setTranslateY(cell.getY() * cellHeight);

                getChildren().add(rect);
            }
        });

        grid.forEach(cell -> {
            var midX = cell.getX() * cellWidth + cellWidth / 2;
            var midY = cell.getY() * cellHeight + cellHeight / 2;

            var text = new Text("" + cell.getX() + "," + cell.getY());
            text.setTranslateX(midX - text.getLayoutBounds().getWidth() / 2);
            text.setTranslateY(midY);

            getChildren().add(text);
        });

        for (int x = 0; x < grid.getWidth(); x++) {
            var line = new Line(x*cellWidth, 0, x*cellWidth, grid.getHeight() * cellHeight);

            getChildren().add(line);
        }

        for (int y = 0; y < grid.getWidth(); y++) {
            var line = new Line(0, y*cellHeight, grid.getWidth() * cellWidth, y*cellHeight);

            getChildren().add(line);
        }
    }
}
