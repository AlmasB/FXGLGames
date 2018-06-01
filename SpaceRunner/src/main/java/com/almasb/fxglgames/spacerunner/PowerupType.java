package com.almasb.fxglgames.spacerunner;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public enum PowerupType {
    BONUS_SCORE_SMALL("star_bronze.png"),
    BONUS_SCORE_MEDIUM("star_silver.png"),
    BONUS_SCORE_HIGH("star_gold.png");

    private String textureName;

    public String getTextureName() {
        return textureName;
    }

    PowerupType(String textureName) {
        this.textureName = textureName;
    }
}
