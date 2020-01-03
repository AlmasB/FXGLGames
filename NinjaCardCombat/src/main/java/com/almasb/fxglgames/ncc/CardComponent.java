package com.almasb.fxglgames.ncc;

import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.ManaIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.Required;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(HealthIntComponent.class)
@Required(ManaIntComponent.class)
public class CardComponent extends Component {

    private HealthIntComponent hp;
    private ManaIntComponent sp;

    private StringProperty name = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();

    private IntegerProperty level = new SimpleIntegerProperty();

    private IntegerProperty atk = new SimpleIntegerProperty();
    private IntegerProperty def = new SimpleIntegerProperty();

    private BooleanBinding alive;

    private CardType type;
    private Rarity rarity;
    private Element element;

    private List<Skill> skills;

    private Card data;

    public CardComponent(Card card) {
        name.set(card.getName());
        description.set(card.getDescription());

        level.set(card.getLevel());
        atk.set(card.getAtk());
        def.set(card.getDef());

        type = card.getType();
        rarity = card.getRarity();
        element = card.getElement();
        skills = card.getSkills();

        data = card;
    }

    @Override
    public void onAdded() {
        alive = hp.zeroProperty().not();
    }

    public Card getData() {
        return data;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public HealthIntComponent getHp() {
        return hp;
    }

    public ManaIntComponent getSp() {
        return sp;
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

    public CardType getType() {
        return type;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public Element getElement() {
        return element;
    }

    public StringProperty toStringProperty() {
        StringProperty prop = new SimpleStringProperty();

        prop.bind(hp.valueProperty().asString().concat(",").concat(sp.valueProperty().asString()).concat(",").concat(level).concat(",").concat(atk).concat(",").concat(def));

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
