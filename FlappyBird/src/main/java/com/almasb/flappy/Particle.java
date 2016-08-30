package com.almasb.flappy;

import javafx.scene.paint.Color;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class Particle {
    double x, y;
    Color color;

    public Particle(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
