package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.component.Component;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CardComponent extends Component {

    private IntegerProperty hp = new SimpleIntegerProperty(60);
    private IntegerProperty sp = new SimpleIntegerProperty(0);

    private IntegerProperty level = new SimpleIntegerProperty(1);
    private IntegerProperty atk = new SimpleIntegerProperty(FXGLMath.random(10, 20));
    private IntegerProperty def = new SimpleIntegerProperty(FXGLMath.random(5, 10));

    private BooleanBinding alive = hp.greaterThan(0);

    public int getHp() {
        return hp.get();
    }

    public IntegerProperty hpProperty() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp.set(hp);
    }

    public int getSp() {
        return sp.get();
    }

    public IntegerProperty spProperty() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp.set(sp);
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public int getAtk() {
        return atk.get();
    }

    public IntegerProperty atkProperty() {
        return atk;
    }

    public void setAtk(int atk) {
        this.atk.set(atk);
    }

    public int getDef() {
        return def.get();
    }

    public IntegerProperty defProperty() {
        return def;
    }

    public void setDef(int def) {
        this.def.set(def);
    }

    public BooleanBinding aliveProperty() {
        return alive;
    }

    public boolean isAlive() {
        return alive.get();
    }

    public boolean isKO() {
        return !isAlive();
    }

    public StringProperty toStringProperty() {
        StringProperty prop = new SimpleStringProperty();

        prop.bind(hp.asString().concat(",").concat(sp).concat(",").concat(level).concat(",").concat(atk).concat(",").concat(def));

        return prop;
    }

    @Override
    public String toString() {
        return "Card{" +
                "hp=" + getHp() +
                ", sp=" + getSp() +
                ", level=" + getLevel() +
                ", atk=" + getAtk() +
                ", def=" + getDef() +
                '}';
    }
}
