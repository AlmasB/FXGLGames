package com.almasb.fxglgames.ncc

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

data class Deck(val cards: List<Card>)

enum class Rarity {
    COMMON, RARE, LEGENDARY
}

enum class CardType {
    WARRIOR, ROGUE, MAGE, SUPPORT
}

enum class Element {
    NEUTRAL, FIRE, WATER, EARTH, AIR
}

data class Card(
        val name: String,
        val description: String,
        val imageName: String,
        val rarity: Rarity,
        val type: CardType,
        val element: Element,

        val skills: List<Skill>,

        val level: Int,
        val hp: Int,
        val sp: Int,
        val atk: Int,
        val def: Int
)

enum class TargetType {
    /* Auto cast */
    NONE, SELF, ALL, ALL_ALLIES, ALL_ENEMIES,

    /* Manual cast (user selects) */
    ALLY, ENEMY
}

data class Skill(
        val name: String,
        val description: String,
        val imageName: String,
        val targetType: TargetType,

        val hpCost: Int,
        val spCost: Int,
        val atkCost: Int,
        val defCost: Int,

        val attr1: Int,
        val attr2: Int,
        val attr3: Int,
        val attr4: Int
)