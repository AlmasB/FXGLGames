package com.almasb.fxglgames.td;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.collection.PropertyMap;
import javafx.beans.property.IntegerProperty;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CurrencyService extends EngineService {

    private PropertyMap map = new PropertyMap();

    public CurrencyService() {
        map.setValue("currency", 0);
    }

    public int getCurrency() {
        return map.getInt("currency");
    }

    public void setCurrency(int value) {
        map.setValue("currency", value);
    }

    public IntegerProperty currencyProperty() {
        return map.intProperty("currency");
    }
}
