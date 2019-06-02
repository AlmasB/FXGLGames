package com.almasb.fxglgames.mario.view;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


/**
 * EntityView for scrollable backgrounds.
 * Ensure that your viewport x, y cannot go < 0.
 * In other words, limit min x, y to 0.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ScrollingBackgroundView extends EntityView {

    // (texture: Texture,
    //                    val orientation: Orientation = Orientation.HORIZONTAL,
    //                    val speed: Double = 1.0)

    private Canvas canvas;
    private GraphicsContext g;

    private Image image;

    private double sx = 0.0;
    private double sy = 0.0;
    
    private Orientation orientation = Orientation.HORIZONTAL;
    private double speed;

    public ScrollingBackgroundView(Texture texture, double speed) {
        image = texture.getImage();
        this.speed = speed;
            
        var viewport = FXGL.getGameScene().getViewport();

        canvas = new Canvas(viewport.getWidth(), viewport.getHeight());
        g = canvas.getGraphicsContext2D();

        if (orientation == Orientation.HORIZONTAL) {
            translateXProperty().addListener((obs, o, x) -> {
                if (x.intValue() < 0)
                    return;

                sx = (x.doubleValue() * speed) % image.getWidth();
                redraw();
            });

            translateXProperty().bind(viewport.xProperty());
        }

//        if (orientation == Orientation.HORIZONTAL) {
//        translateXProperty().addListener { _, _, x ->
//
//        //check(x.toInt() >= 0) { "Background x cannot be < 0" }
//
//        if (x.toInt() < 0)
//        return@addListener
//
//                sx = (x.toDouble() * speed) % image.getWidth()
//                        redraw()
//                        }
//
//                        translateXProperty().bind(viewport.xProperty())
//                        } else {
//                        translateYProperty().addListener { _, _, y ->
//
//                        //check(y.toInt() >= 0) { "Background y cannot be < 0" }
//
//                        if (y.toInt() < 0)
//        return@addListener
//
//                sy = (y.toDouble() * speed) % image.getHeight()
//                        redraw()
//                        }
//
//                        translateYProperty().bind(viewport.yProperty())
//                        }
//
        addNode(canvas);

        redraw();
    }

    private void redraw() {
        g.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

        if (orientation == Orientation.HORIZONTAL) {
            redrawX();
        } else {
            redrawY();
        }
    }

    private void redrawX() {
        var w = canvas.getWidth();
        var h = canvas.getHeight();

        var overflowX = sx + w > image.getWidth();

        if (overflowX) {
            w = image.getWidth() - sx;
        }

        g.drawImage(image, sx, sy, w, h,
                0.0, 0.0, w, h);

        if (overflowX) {
            g.drawImage(image, 0.0, 0.0, canvas.getWidth() - w, h,
                    w, 0.0, canvas.getWidth() - w, h);
        }
    }

    private void redrawY() {
        var w = canvas.getWidth();
        var h = canvas.getHeight();

        var overflowY = sy + h > image.getHeight();

        if (overflowY) {
            h = image.getHeight() - sy;
        }

        g.drawImage(image, sx, sy, w, h,
                0.0, 0.0, w, h);

        if (overflowY) {
            g.drawImage(image, 0.0, 0.0, w, canvas.getHeight() - h,
                    0.0, h, w, canvas.getHeight() - h);
        }
    }
}
