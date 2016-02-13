/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.drop;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityFactory {

    public enum EntityType {
        DROPLET, BUCKET
    }

    private static AssetLoader assetLoader;

    static {
        assetLoader = GameApplication.getService(ServiceType.ASSET_LOADER);
    }

    public static Entity newDroplet(double x, double y) {
        GameEntity droplet = new GameEntity();
        droplet.getTypeComponent().setValue(EntityType.DROPLET);
        droplet.getPositionComponent().setValue(x, y);
        droplet.getMainViewComponent().setView(new EntityView(assetLoader.loadTexture("droplet.png")), true);

        droplet.addComponent(new CollidableComponent(true));
        droplet.addControl(new DropletControl());

        return droplet;
    }

    public static Entity newBucket(double x, double y) {
        GameEntity bucket = new GameEntity();
        bucket.getTypeComponent().setValue(EntityType.BUCKET);
        bucket.getPositionComponent().setValue(x, y);
        bucket.getMainViewComponent().setView(new EntityView(assetLoader.loadTexture("bucket.png")), true);

        bucket.addComponent(new CollidableComponent(true));
        bucket.addControl(new BucketControl());

        return bucket;
    }
}
