package com.almasb.tictactoe;

import com.almasb.fxgl.app.FXGL;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Tile extends StackPane {

    private TicTacToeApp app;
    private TileValue value = TileValue.NONE;
    private Text text = new Text();

    public Tile() {
        app = FXGL.getAppCast();

        Rectangle bg = new Rectangle(app.getWidth() / 3, app.getHeight() / 3, Color.rgb(13, 222, 236));

        Rectangle bg2 = new Rectangle(app.getWidth() / 4, app.getHeight() / 4, Color.rgb(250, 250, 250, 0.25));
        bg2.setArcWidth(25);
        bg2.setArcHeight(25);

        text.setFont(Font.font(72));

        getChildren().addAll(bg, bg2, text);

        setOnMouseClicked(e -> app.onUserMove(this));
    }

    public TileValue getValue() {
        return value;
    }

    public boolean mark(TileValue value) {
        if (this.value != TileValue.NONE)
            return false;

        this.value = value;
        text.setText(value.symbol);
        return true;
    }
}
