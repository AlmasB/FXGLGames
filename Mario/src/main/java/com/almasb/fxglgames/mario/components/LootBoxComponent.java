package com.almasb.fxglgames.mario.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.ViewComponent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LootBoxComponent extends Component {

    private boolean draw = false;
    private int drawRow = 0;

    private Canvas canvas = new Canvas(70, 70);
    private GraphicsContext g = canvas.getGraphicsContext2D();

    private Image image;

    private ViewComponent view;

    public void open() {
        draw = true;

        g.setFill(Color.LIGHTGREEN);

        canvas.setEffect(new BoxBlur(2, 2, 2));

        view.addChild(canvas);

        image = FXGL.image("loot_box_sign.png");
    }

    @Override
    public void onUpdate(double tpf) {
        if (!draw)
            return;

        if (drawRow >= 70) {
            spawnLoot();
            entity.removeFromWorld();
            return;
        }

        for (int x = 0; x < image.getWidth(); x++) {
            var color = image.getPixelReader().getColor(x, drawRow);
            if (!color.equals(Color.TRANSPARENT)) {
                g.fillRect(x, drawRow, 1, 1);
            }
        }

        drawRow++;
    }

    private void spawnLoot() {
        FXGL.spawn("coin", new SpawnData(getEntity().getPosition()).put("width", 70).put("height", 70));
    }
}
