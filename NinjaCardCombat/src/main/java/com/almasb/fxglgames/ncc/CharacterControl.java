package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CharacterControl extends AbstractControl {

    private IntegerProperty hp = new SimpleIntegerProperty(100);

    public int getHp() {
        return hp.get();
    }

    public IntegerProperty hpProperty() {
        return hp;
    }

    private IntegerProperty manaNeutral = new SimpleIntegerProperty(1);
    private IntegerProperty manaFire = new SimpleIntegerProperty(2);
    private IntegerProperty manaWater = new SimpleIntegerProperty(3);
    private IntegerProperty manaEarth = new SimpleIntegerProperty(4);
    private IntegerProperty manaAir = new SimpleIntegerProperty(5);
    private IntegerProperty manaLightning = new SimpleIntegerProperty(6);

    public int getManaNeutral() {
        return manaNeutral.get();
    }

    public IntegerProperty manaNeutralProperty() {
        return manaNeutral;
    }

    public int getManaFire() {
        return manaFire.get();
    }

    public IntegerProperty manaFireProperty() {
        return manaFire;
    }

    public int getManaWater() {
        return manaWater.get();
    }

    public IntegerProperty manaWaterProperty() {
        return manaWater;
    }

    public int getManaEarth() {
        return manaEarth.get();
    }

    public IntegerProperty manaEarthProperty() {
        return manaEarth;
    }

    public int getManaAir() {
        return manaAir.get();
    }

    public IntegerProperty manaAirProperty() {
        return manaAir;
    }

    public int getManaLightning() {
        return manaLightning.get();
    }

    public IntegerProperty manaLightningProperty() {
        return manaLightning;
    }

    private ObservableList<Entity> cards = FXCollections.observableArrayList();

    public ObservableList<Entity> getCards() {
        return cards;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

    }
}
