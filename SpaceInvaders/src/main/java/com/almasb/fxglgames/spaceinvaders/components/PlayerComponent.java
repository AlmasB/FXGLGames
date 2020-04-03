/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.spaceinvaders.components;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.spaceinvaders.Config;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(InvincibleComponent.class)
public class PlayerComponent extends Component {

    private InvincibleComponent invincibility;

    private double dx = 0;
    private double attackSpeed = Config.PLAYER_ATTACK_SPEED;

    private boolean canShoot = true;
    private double lastTimeShot = 0;

    @Override
    public void onUpdate(double tpf) {
        dx = Config.PLAYER_MOVE_SPEED * tpf;

        if (!canShoot) {
            if ((getGameTimer().getNow() - lastTimeShot) >= 1.0 / attackSpeed) {
                canShoot = true;
            }
        }
    }

    public void left() {
        if (getEntity().getX() - dx >= 0)
            getEntity().translateX(-dx);

        spawnParticles();
    }

    public void right() {
        if (getEntity().getX() + getEntity().getWidth() + dx <= Config.WIDTH)
            getEntity().translateX(dx);

        spawnParticles();
    }

    public void shoot() {
        if (!canShoot)
            return;

        canShoot = false;
        lastTimeShot = getGameTimer().getNow();

        spawn("Laser", new SpawnData(0, 0).put("owner", getEntity()));

        play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    public void shootRedLaser() {
        spawn("RedLaser", entity.getCenter());
    }

    public void shootLaser() {
        if (getd("laserMeter") == Config.LASER_METER_MAX) {
            laserBeamActive = true;

            Entity beam = spawn("LaserBeam");
            beam.xProperty().bind(getEntity().xProperty().add(21));
            beam.setY(-10);
            beam.setOnNotActive(() -> laserBeamActive = false);
        }
    }

    private boolean laserBeamActive = false;

    public boolean isLaserBeamActive() {
        return laserBeamActive;
    }

    public void enableInvincibility() {
        invincibility.setValue(true);
    }

    public void disableInvincibility() {
        invincibility.setValue(false);
    }

    public void increaseAttackSpeed(double value) {
        attackSpeed += value;
    }

    private Image particle;

    private void spawnParticles() {
        if (particle == null) {
            particle = image("player2.png", 40, 30);
        }

        // TODO: move to factory
        // TODO: add z index layer mapping in Config
        entityBuilder()
                .at(getEntity().getCenter().subtract(particle.getWidth() / 2, particle.getHeight() / 2))
                .view(new Texture(particle))
                .zIndex(-300)
                .with(new ExpireCleanComponent(Duration.seconds(0.33)).animateOpacity())
                .buildAndAttach();
    }
}
